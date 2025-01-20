package org.example.workspace.factory;

import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.dto.request.UsersReqDto;
import org.example.workspace.entity.Role;
import org.example.workspace.entity.Users;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.entity.code.SnsType;
import org.example.workspace.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class ObjectFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsersReqDto createUsersReqDto() {
        return UsersReqDto
                .builder()
                .loginId("kwang")
                .password("!work1234")
                .confirmPassword("!work1234")
                .userName("최광섭")
                .nickname("최광섭")
                .email("test@gmail.com")
                .phoneNumber("01012341234")
                .userSnsList(
                        List.of(
                                createUsersSnsReqDto(SnsType.FACEBOOK),
                                createUsersSnsReqDto(SnsType.INSTAGRAM)
                        )
                )
                .build();
    }

    public UsersSnsReqDto createUsersSnsReqDto(SnsType snsType) {
        return UsersSnsReqDto.builder()
                .snsType(snsType)
                .snsUsername("snsUser")
                .build();
    }

    public Role createRole(RoleType roleType) {
        return Role.builder()
                .roleType(roleType)
                .build();
    }

    public UserDetails createRoleUserDetails(AuthReqDto authReqDto) {
        Users users = Users.builder()
                .loginId(authReqDto.username())
                .password(passwordEncoder.encode(authReqDto.password()))
                .role(createRole(RoleType.ROLE_ARTIST))
                .isActivated(true)
                .build();

        return CustomUserDetails.create(users, users.getRole().getRoleType());
    }
}
