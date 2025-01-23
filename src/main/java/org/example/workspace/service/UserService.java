package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.request.*;
import org.example.workspace.dto.response.UserResDto;
import org.example.workspace.entity.Contents;
import org.example.workspace.entity.Role;
import org.example.workspace.entity.User;
import org.example.workspace.entity.code.MenuType;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.exception.AlreadyRegisteredIdentifierFieldException;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.exception.InvalidPasswordException;
import org.example.workspace.exception.InvalidTokenException;
import org.example.workspace.mapper.UserMapper;
import org.example.workspace.repository.RoleRepository;
import org.example.workspace.repository.UserRepository;
import org.example.workspace.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSnsService userSnsService;
    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final UserVerificationService userVerificationService;
    private final ContentsService contentsService;
    private final UserMenuService userMenuService;

    @Transactional(readOnly = true)
    public UserResDto getDetail(Long id) {
        User user = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));

        return userMapper.toDto(user);
    }

    public UserResDto create(UserCreateReqDto dto) {
        checkUserDuplicateLoginId(dto.loginId());
        checkDuplicateEmailAndWorkspaceName(null, dto.email(), dto.workspaceName());
        checkConfirmPassword(dto.password(), dto.confirmPassword());

        Role role = roleRepository.findByRoleType(RoleType.ROLE_ARTIST)
                .orElseThrow(() -> new EntityNotFoundException(Role.class, null));
        String encodePassword = passwordEncoder.encode(dto.password());

        User user = User.create(dto, encodePassword, role);
        repository.save(user);

        userSnsService.saveAll(user, dto.userSnsList());


        userMenuService.savaAll(
                user.getId(),
                Set.of(
                        new UserMenuReqDto(null, null, MenuType.SHOWCASE),
                        new UserMenuReqDto(null, null, MenuType.ARTWORK),
                        new UserMenuReqDto(null, null, MenuType.PROFILE)
                )
        );

        String token = jwtUtil.generateEmailVerifyToken(user.getEmail(), user.getId());
        mailService.sendSignupConfirmMail(token, user.getEmail());

        return getDetail(user.getId());
    }


    public UserResDto update(Long id, UserUpdateReqDto dto) {
        User user = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
        checkDuplicateEmailAndWorkspaceName(id, dto.email(), dto.workspaceName());

        Contents logo = dto.logoId() != null ? contentsService.getEntity(dto.logoId()) : null;

        user.update(dto, logo);
        repository.save(user);
        userSnsService.saveAll(user, dto.userSnsList());

        return getDetail(user.getId());
    }

    private void checkDuplicateEmailAndWorkspaceName(Long userId, String email, String workspaceName) {
        checkUserDuplicateEmail(userId, email);
        checkUserDuplicateWorkspaceName(userId, workspaceName);
    }

    private void checkUserDuplicateWorkspaceName(Long userId, String workspaceName) {
        User existUser = repository.findByWorkspaceNameAndIsDeletedFalse(workspaceName)
                .orElse(null);
        if ((userId == null && existUser != null) ||
                (userId != null && existUser != null && !existUser.getId().equals(userId)))
            throw new AlreadyRegisteredIdentifierFieldException(ApplicationConstant.Exception.EXCEPTION_PARAM_WORKSPACE_NAME);
    }

    private void checkUserDuplicateLoginId(String loginId) {
        Optional<User> existUser = repository.findByLoginIdAndIsDeletedFalse(loginId);
        if (existUser.isPresent())
            throw new AlreadyRegisteredIdentifierFieldException(ApplicationConstant.Exception.EXCEPTION_PARAM_LOGIN_ID);
    }

    private void checkUserDuplicateEmail(Long userId, String email) {
        User existUser = repository.findByEmailAndIsDeletedFalse(email)
                .orElse(null);
        if ((userId == null && existUser != null) ||
                (userId != null && existUser != null && !existUser.getId().equals(userId)))
            throw new AlreadyRegisteredIdentifierFieldException(ApplicationConstant.Exception.EXCEPTION_PARAM_EMAIL);
    }

    private void checkConfirmPassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new InvalidPasswordException();
    }

    public Boolean emailVerify(String token) {
        if (jwtUtil.isTokenExpired(token))
            throw new InvalidTokenException();
        Long userId = jwtUtil.extractId(token);
        String userEmail = jwtUtil.extractSubject(token, JwtUtil.TokenType.EMAIL_VERIFY);

        User user = repository.findByIdAndEmailAndIsDeletedFalse(userId, userEmail)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));

        user.isVerified();
        repository.save(user);

        return true;
    }

    public Boolean recovery(String email) {
        User user = repository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException(User.class, null));

        String verificationCode = userVerificationService.create(user);
        String token = jwtUtil.generateRecoveryToken(user.getId(), verificationCode);
        mailService.sendUserRecovery(user.getLoginId(), token, user.getEmail());
        return true;
    }

    public Boolean updatePassword(UserPasswordReqDto dto) {
        String token = dto.token();
        String idString = jwtUtil.extractSubject(token, JwtUtil.TokenType.RECOVERY);
        Long id = Long.parseLong(idString);

        checkInvalidPasswordUpdateRequest(id, dto);

        User user = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, id));
        user.updatePassword(passwordEncoder.encode(dto.password()));
        repository.save(user);
        userVerificationService.completeVerify(user);
        return true;
    }

    private void checkInvalidPasswordUpdateRequest(Long id, UserPasswordReqDto dto) {
        userVerificationService.checkVerification(id, jwtUtil.extractCode(dto.token()));
        checkConfirmPassword(dto.password(), dto.confirmPassword());
    }

    @Transactional(readOnly = true)
    public Boolean getDuplicateWhether(UserDuplicateReqDto dto) {
        String value = dto.value();
        Optional<User> user = switch (dto.type()) {
            case EMAIL -> repository.findByEmailAndIsDeletedFalse(value);
            case LOGIN_ID -> repository.findByLoginIdAndIsDeletedFalse(value);
            case WORKSPACE_NAME -> repository.findByWorkspaceNameAndIsDeletedFalse(value);
        };

        return user.isPresent();
    }
}
