package org.example.workspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.entity.code.RoleName;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
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
    private RoleName roleName;

    @Column(name = "role_desc", length = ApplicationConstant.Entity.MAX_LENGTH_TEXT_NORMAL)
    private String roleDesc;

    @OneToMany(mappedBy = "role")
    private Set<Users> users = new LinkedHashSet<>();

}