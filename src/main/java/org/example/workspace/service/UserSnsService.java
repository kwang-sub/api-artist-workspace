package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.entity.Users;
import org.example.workspace.entity.UsersSns;
import org.example.workspace.repository.UsersSnsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSnsService {

    private final UsersSnsRepository usersSnsRepository;

    public void saveAll(Users users, List<UsersSnsReqDto> usersSnsReqDtos) {

        List<UsersSns> usersSns = usersSnsReqDtos.stream().map(it -> UsersSns.create(users, it))
                .toList();
        usersSnsRepository.saveAll(usersSns);
    }
}
