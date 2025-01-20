package org.example.workspace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.entity.code.SnsType;

@Getter
@Entity
@Table(name = "tbl_users_sns")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersSns extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "sns_type", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private SnsType snsType;

    @Column(name = "sns_username", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    private String snsUsername;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    public static UsersSns create(Users users, UsersSnsReqDto dto) {
        UsersSns usersSns = UsersSns.builder()
                .snsType(dto.snsType())
                .snsUsername(dto.snsUsername())
                .users(users)
                .build();
        users.addSns(usersSns);
        return usersSns;
    }
}