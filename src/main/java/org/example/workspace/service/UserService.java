package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UsersReqDto;
import org.example.workspace.dto.response.UsersResDto;
import org.example.workspace.entity.Role;
import org.example.workspace.entity.Users;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.exception.ExistsLoginIdException;
import org.example.workspace.exception.InvalidPasswordException;
import org.example.workspace.mapper.UserMapper;
import org.example.workspace.repository.RoleRepository;
import org.example.workspace.repository.UsersRepository;
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

    @Transactional(readOnly = true)
    public UsersResDto getDetail(Long id) {
        Users user = usersRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(Users.class, id));

        return userMapper.toDto(user);
    }

    public UsersResDto create(UsersReqDto dto) {
        Optional<Users> existUser = usersRepository.findByLoginIdAndIsDeletedFalse(dto.getLoginId());
        if (existUser.isPresent()) throw new ExistsLoginIdException();
        if (!dto.getPassword().equals(dto.getConfirmPassword()))
            throw new InvalidPasswordException();

        Role role = roleRepository.findByRoleType(RoleType.ROLE_ARTIST)
                .orElseThrow(() -> new EntityNotFoundException(Role.class, null));

        String encodePassword = passwordEncoder.encode(dto.getPassword());
        Users user = Users.create(dto, encodePassword, role);
        usersRepository.save(user);

        return userMapper.toDto(user);
    }
}
