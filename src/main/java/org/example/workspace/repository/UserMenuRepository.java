package org.example.workspace.repository;

import org.example.workspace.entity.UserMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMenuRepository extends JpaRepository<UserMenu, Long> {
    List<UserMenu> findByUserId(Long id);
}
