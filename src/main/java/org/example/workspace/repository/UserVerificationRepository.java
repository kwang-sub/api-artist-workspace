package org.example.workspace.repository;

import org.example.workspace.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Integer> {
}
