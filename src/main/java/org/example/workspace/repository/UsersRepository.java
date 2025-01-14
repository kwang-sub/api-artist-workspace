package org.example.workspace.repository;

import org.example.workspace.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByLoginIdAndIsDeletedFalse(String username);
}
