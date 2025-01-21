package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserSns;
import org.example.workspace.repository.UsersSnsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSnsService {

    private final UsersSnsRepository usersSnsRepository;

    public void saveAll(User user, List<UsersSnsReqDto> usersSnsReqDtos) {

        List<UserSns> userSns = usersSnsReqDtos.stream().map(it -> UserSns.create(user, it))
                .toList();
        usersSnsRepository.saveAll(userSns);
    }
}
