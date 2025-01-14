package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.response.UsersResDto;
import org.example.workspace.entity.Users;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.mapper.UserMapper;
import org.example.workspace.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UsersResDto getDetail(Long id) {
        Users user = usersRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(Users.class, id));

        return userMapper.toDto(user);
    }
}
