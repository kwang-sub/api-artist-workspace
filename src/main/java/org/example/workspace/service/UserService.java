package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UserPasswordReqDto;
import org.example.workspace.dto.request.UserReqDto;
import org.example.workspace.dto.response.UserResDto;
import org.example.workspace.entity.Role;
import org.example.workspace.entity.User;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.exception.*;
import org.example.workspace.mapper.UserMapper;
import org.example.workspace.repository.RoleRepository;
import org.example.workspace.repository.UserRepository;
import org.example.workspace.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSnsService userSnsService;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final UserVerificationService userVerificationService;

    @Transactional(readOnly = true)
    public UserResDto getDetail(Long id) {
        User user = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));

        return userMapper.toDto(user);
    }

    public UserResDto create(UserReqDto dto) {
        checkInvalidCreateRequest(dto);

        Role role = roleRepository.findByRoleType(RoleType.ROLE_ARTIST)
                .orElseThrow(() -> new EntityNotFoundException(Role.class, null));
        String encodePassword = passwordEncoder.encode(dto.password());

        User user = User.create(dto, encodePassword, role);
        repository.save(user);

        userSnsService.saveAll(user, dto.userSnsList());

        String token = jwtUtil.generateEmailVerifyToken(user.getEmail(), user.getId());
        mailService.sendSignupConfirmMail(token, user.getEmail());

        return getDetail(user.getId());
    }

    private void checkInvalidCreateRequest(UserReqDto dto) {
        checkUserDuplicateLoginId(dto.loginId());
        checkUserDuplicateEmail(dto.email());
        checkConfirmPassword(dto.password(), dto.confirmPassword());
    }

    private void checkUserDuplicateLoginId(String loginId) {
        Optional<User> existUser = repository.findByLoginIdAndIsDeletedFalse(loginId);
        if (existUser.isPresent()) throw new ExistsLoginIdException();
    }

    private void checkUserDuplicateEmail(String email) {
        Optional<User> existUser = repository.findByEmailAndIsDeletedFalse(email);
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
        String userEmail = jwtUtil.extractSubject(token, JwtUtil.TokenType.EMAIL_VERIFY);

        User user = repository.findByIdAndEmailAndIsDeletedFalse(userId, userEmail)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));

        user.isVerified();
        repository.save(user);

        return true;
    }

    public Boolean recovery(String email) {
        User user = repository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class, null));

        String verificationCode = userVerificationService.create(user);
        String token = jwtUtil.generateRecoveryToken(user.getId(), verificationCode);
        mailService.sendUserRecovery(user.getLoginId(), token, user.getEmail());
        return true;
    }

    public Boolean updatePassword(UserPasswordReqDto dto) {
        String token = dto.token();
        String idString = jwtUtil.extractSubject(token, JwtUtil.TokenType.RECOVERY);
        Long id = Long.parseLong(idString);

        checkInvalidPasswordUpdateRequest(id, dto);

        User user = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
        user.updatePassword(passwordEncoder.encode(dto.password()));
        repository.save(user);
        return true;
    }

    private void checkInvalidPasswordUpdateRequest(Long id, UserPasswordReqDto dto) {
        userVerificationService.checkVerification(id, jwtUtil.extractCode(dto.token()));
        checkConfirmPassword(dto.password(), dto.confirmPassword());
    }
}
