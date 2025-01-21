package org.example.workspace.repository;

import org.example.workspace.entity.UserSns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersSnsRepository extends JpaRepository<UserSns, Long> {
}
