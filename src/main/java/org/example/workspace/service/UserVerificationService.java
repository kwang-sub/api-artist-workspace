package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserVerification;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.exception.InvalidTokenException;
import org.example.workspace.repository.UserVerificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserVerificationService {

    private final UserVerificationRepository repository;

    public String create(User user) {
        UserVerification verification = repository.findByUserId(user.getId())
                .map(existingVerification -> {
                    existingVerification.updateCode();
                    return existingVerification;
                })
                .orElse(UserVerification.create(user));

        repository.save(verification);
        return verification.getVerificationCode();
    }

    public void checkVerification(Long userId, String code) {
        UserVerification userVerification = repository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserVerification.class, null));

        if (!userVerification.getVerificationCode().equals(code))
            throw new InvalidTokenException();
    }

    public void completeVerify(User user) {
        UserVerification userVerification = repository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(UserVerification.class, null));
        repository.delete(userVerification);
    }
}
