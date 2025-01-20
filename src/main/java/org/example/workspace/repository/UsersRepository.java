package org.example.workspace.repository;

import org.example.workspace.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByLoginIdAndIsDeletedFalse(String username);

    Optional<Users> findByEmailAndIsDeletedFalse(String username);

    Optional<Users> findByIdAndIsDeletedFalse(Long id);


    Optional<Users> findByIdAndEmailAndIsDeletedFalse(Long userId, String userEmail);
}
