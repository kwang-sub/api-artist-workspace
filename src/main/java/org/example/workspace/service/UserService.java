package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UsersReqDto;
import org.example.workspace.dto.response.UsersResDto;
import org.example.workspace.entity.Role;
import org.example.workspace.entity.Users;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.exception.*;
import org.example.workspace.mapper.UserMapper;
import org.example.workspace.repository.RoleRepository;
import org.example.workspace.repository.UsersRepository;
import org.example.workspace.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSnsService userSnsService;
    private final MailService mailService;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public UsersResDto getDetail(Long id) {
        Users users = usersRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(Users.class, id));

        return userMapper.toDto(users);
    }

    public UsersResDto create(UsersReqDto dto) {
        checkInvalidRequest(dto);

        Role role = roleRepository.findByRoleType(RoleType.ROLE_ARTIST)
                .orElseThrow(() -> new EntityNotFoundException(Role.class, null));
        String encodePassword = passwordEncoder.encode(dto.password());

        Users users = Users.create(dto, encodePassword, role);
        usersRepository.save(users);

        userSnsService.saveAll(users, dto.userSnsList());

        String token = jwtUtil.generateEmailVerifyToken(users.getEmail(), users.getId());
        mailService.sendSignupConfirmMail(token, users.getEmail());

        return userMapper.toDto(users);
    }

    private void checkInvalidRequest(UsersReqDto dto) {
        checkUserDuplicateLoginId(dto.loginId());
        checkUserDuplicateEmail(dto.email());
        checkConfirmPassword(dto.password(), dto.confirmPassword());

    }

    private void checkUserDuplicateLoginId(String loginId) {
        Optional<Users> existUser = usersRepository.findByLoginIdAndIsDeletedFalse(loginId);
        if (existUser.isPresent()) throw new ExistsLoginIdException();
    }

    private void checkUserDuplicateEmail(String email) {
        Optional<Users> existUser = usersRepository.findByEmailAndIsDeletedFalse(email);
        if (existUser.isPresent()) throw new ExistsEmailException();
    }

    private void checkConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new InvalidPasswordException();
    }

    public Boolean emailVerify(String token) {
        if (jwtUtil.isTokenExpired(token))
            throw new InvalidTokenException();
        Long userId = jwtUtil.extractId(token);
        String userEmail = jwtUtil.extractSubject(token);

        Users user = usersRepository.findByIdAndEmailAndIsDeletedFalse(userId, userEmail)
                .orElseThrow(() -> new EntityNotFoundException(Users.class, userId));

        user.isVerified();
        usersRepository.save(user);

        return true;
    }
}
