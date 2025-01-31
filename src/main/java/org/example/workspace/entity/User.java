package org.example.workspace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.request.UserCreateReqDto;
import org.example.workspace.dto.request.UserUpdateReqDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login_id", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String loginId;

    @Column(name = "password", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "workspace_name", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String workspaceName;

    @Column(name = "email", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String email;

    @Column(name = "phone_number", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String phoneNumber;

    @Column(name = "bio", length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String bio;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated;

    @Column(name = "is_use_temp_password", nullable = false)
    private Boolean isUseTempPassword;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<UserSns> userSnsList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "logo_file_id")
    private Contents logo;

    public static User create(UserCreateReqDto dto, String encodedPassword, Role role) {
        return User.builder()
                .loginId(dto.loginId())
                .password(encodedPassword)
                .userName(dto.userName())
                .nickname(dto.nickname())
                .workspaceName(dto.workspaceName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .isActivated(false)
                .isUseTempPassword(false)
                .role(role)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addSns(UserSns userSns) {
        this.userSnsList.add(userSns);
    }

    public void isVerified() {
        this.isActivated = true;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void update(UserUpdateReqDto dto, Contents logo) {
        this.userName = dto.userName();
        this.nickname = dto.nickname();
        this.workspaceName = dto.workspaceName();
        this.phoneNumber = dto.phoneNumber();
        this.bio = dto.bio();
        this.logo = logo;
    }

    public void userSnsRemoveAll(List<UserSns> delteList) {
        this.userSnsList.removeAll(delteList);
    }
}