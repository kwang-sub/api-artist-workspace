package org.example.workspace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.entity.code.RoleType;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_role")
public class Role extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "role_code", length = ApplicationConstant.Entity.MAX_LENGTH_CODE)
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_SMALL)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "role_desc", length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_NORMAL)
    private String roleDesc;

    @OneToMany(mappedBy = "role")
    @Builder.Default
    private Set<Users> users = new LinkedHashSet<>();

}