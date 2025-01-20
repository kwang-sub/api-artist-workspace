package org.example.workspace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.request.UsersReqDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_users")
public class Users extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login_id", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String loginId;

    @Column(name = "password", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_NORMAL)
    private String password;

    @Column(name = "user_name", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String userName;

    @Column(name = "nickname", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_NORMAL)
    private String nickname;

    @Column(name = "email", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String email;


    @Column(name = "phone_number", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String phoneNumber;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated;

    @Column(name = "is_use_temp_password", nullable = false)
    private Boolean isUseTempPassword;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "users")
    @Builder.Default
    private List<UsersSns> userSnsList = new ArrayList<>();

    public static Users create(UsersReqDto dto, String encodePassword, Role role) {
        return Users.builder()
                .loginId(dto.loginId())
                .password(encodePassword)
                .userName(dto.userName())
                .nickname(dto.nickname())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .isActivated(false)
                .isUseTempPassword(false)
                .role(role)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Users users)) return false;
        return Objects.equals(id, users.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addSns(UsersSns usersSns) {
        this.userSnsList.add(usersSns);
    }
}