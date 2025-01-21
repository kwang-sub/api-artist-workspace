package org.example.workspace.repository;

import org.example.workspace.entity.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentsRepository extends JpaRepository<Contents, Long> {

    Optional<Contents> findByIdAndIsDeletedFalse(Long id);
}
