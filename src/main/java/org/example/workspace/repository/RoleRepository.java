package org.example.workspace.repository;

import org.example.workspace.entity.Role;
import org.example.workspace.entity.Users;
import org.example.workspace.entity.code.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleType(RoleType roleType);

}
