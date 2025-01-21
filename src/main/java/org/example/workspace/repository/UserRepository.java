package org.example.workspace.repository;

import org.example.workspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginIdAndIsDeletedFalse(String username);

    Optional<User> findByEmailAndIsDeletedFalse(String username);

    Optional<User> findByIdAndIsDeletedFalse(Long id);


    Optional<User> findByIdAndEmailAndIsDeletedFalse(Long userId, String userEmail);
}
