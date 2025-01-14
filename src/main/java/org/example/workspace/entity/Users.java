package org.example.workspace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.workspace.common.ApplicationConstant;

import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_users")
public class Users extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login_id", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String loginId;

    @Column(name = "password", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_NORMAL)
    private String password;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "email", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String email;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated;

    @Column(name = "is_use_temp_password", nullable = false)
    private Boolean isUseTempPassword;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Users users)) return false;
        return Objects.equals(id, users.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}