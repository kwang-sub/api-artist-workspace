package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserSns;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.repository.UsersSnsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSnsService {

    private final UsersSnsRepository repository;

    public void saveAll(User user, List<UsersSnsReqDto> usersSnsReqDtoList) {
        List<UserSns> userSnsList = user.getUserSnsList();

        Map<Boolean, List<UsersSnsReqDto>> partitionedDtos =
                usersSnsReqDtoList.stream()
                        .collect(Collectors.partitioningBy(dto -> dto.id() != null));

        List<UsersSnsReqDto> updateReqList = partitionedDtos.get(true);
        List<UsersSnsReqDto> insertList = partitionedDtos.get(false);

        processUpdates(user, userSnsList, updateReqList);
        processInserts(user, insertList);
    }

    private void processUpdates(User user, List<UserSns> userSnsList, List<UsersSnsReqDto> updateReqList) {
        Set<Long> updateIds = updateReqList.stream()
                .map(UsersSnsReqDto::id)
                .collect(Collectors.toSet());

        List<UserSns> toUpdate = updateReqList.stream()
                .map(reqDto -> {
                    UserSns entity = userSnsList.stream()
                            .filter(sns -> sns.getId().equals(reqDto.id()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException(UserSns.class, reqDto.id()));
                    entity.update(reqDto);
                    return entity;
                })
                .collect(Collectors.toList());

        repository.saveAll(toUpdate);

        List<UserSns> toDelete = userSnsList.stream()
                .filter(sns -> !updateIds.contains(sns.getId()))
                .collect(Collectors.toList());
        user.userSnsRemoveAll(toDelete);
        repository.deleteAll(toDelete);
    }

    private void processInserts(User user, List<UsersSnsReqDto> insertList) {
        List<UserSns> toInsert = insertList.stream()
                .map(dto -> UserSns.create(user, dto))
                .collect(Collectors.toList());

        repository.saveAll(toInsert);
    }
}
