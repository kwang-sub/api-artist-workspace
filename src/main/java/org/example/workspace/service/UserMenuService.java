package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UserMenuReqDto;
import org.example.workspace.dto.response.MenuResDto;
import org.example.workspace.dto.response.UserMenuResDto;
import org.example.workspace.entity.Contents;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserMenu;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.mapper.UserMenuMapper;
import org.example.workspace.repository.UserMenuRepository;
import org.example.workspace.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserMenuService {
    private final UserMenuMapper mapper;
    private final UserMenuRepository repository;
    private final ContentsService contentsService;
    private final UserRepository userRepository;

    public MenuResDto savaAll(Long userId, Set<UserMenuReqDto> userMenuReqDtos) {
        List<UserMenu> userMenuList = repository.findByUserId(userId);

        Map<Boolean, List<UserMenuReqDto>> partitionedDtos =
                userMenuReqDtos.stream()
                        .collect(Collectors.partitioningBy(dto -> dto.id() != null));

        List<UserMenuReqDto> updateReqList = partitionedDtos.get(true);
        List<UserMenuReqDto> insertList = partitionedDtos.get(false);

        processUpdates(userMenuList, updateReqList);
        processInserts(userId, insertList);

        List<UserMenuResDto> result = repository.findByUserId(userId)
                .stream().map(mapper::toDto)
                .toList();
        return new MenuResDto(result);
    }

    private void processUpdates(List<UserMenu> userMenuList, List<UserMenuReqDto> updateReqList) {
        Set<Long> updateIds = updateReqList.stream()
                .map(UserMenuReqDto::id)
                .collect(Collectors.toSet());

        List<UserMenu> toUpdate = updateReqList.stream()
                .map(reqDto -> {
                    UserMenu entity = userMenuList.stream()
                            .filter(sns -> sns.getId().equals(reqDto.id()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException(UserMenu.class, reqDto.id()));
                    Contents contents = reqDto.contentsId() != null ? contentsService.getEntity(reqDto.contentsId()) : null;
                    entity.update(contents, reqDto.menuType());
                    return entity;
                })
                .collect(Collectors.toList());

        repository.saveAll(toUpdate);

        List<UserMenu> toDelete = userMenuList.stream()
                .filter(sns -> !updateIds.contains(sns.getId()))
                .collect(Collectors.toList());
        repository.deleteAll(toDelete);
    }

    private void processInserts(Long userId, List<UserMenuReqDto> insertList) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));
        List<UserMenu> toInsert = insertList.stream()
                .map(dto -> {
                            Contents contents = dto.contentsId() != null ? contentsService.getEntity(dto.contentsId()) : null;
                            return UserMenu.create(user, contents, dto.menuType());
                        }
                )
                .collect(Collectors.toList());

        repository.saveAll(toInsert);
    }

    public MenuResDto getUserMenus(Long userId) {
        List<UserMenuResDto> list = repository.findByUserId(userId)
                .stream().map(mapper::toDto)
                .toList();
        return new MenuResDto(list);
    }
}
