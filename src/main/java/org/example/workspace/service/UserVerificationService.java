package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserVerification;
import org.example.workspace.repository.UserVerificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserVerificationService {

    private final UserVerificationRepository repository;

    public String create(User user) {
        UserVerification verification = UserVerification.create(user);
        repository.save(verification);
        return verification.getVerificationCode();
    }
}
