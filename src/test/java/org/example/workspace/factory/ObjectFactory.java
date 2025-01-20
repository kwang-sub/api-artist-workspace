package org.example.workspace.factory;

import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.dto.request.UsersReqDto;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.entity.Role;
import org.example.workspace.entity.Users;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.entity.code.SnsType;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.repository.RoleRepository;
import org.example.workspace.repository.UsersRepository;
import org.example.workspace.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@TestComponent
public class ObjectFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UsersReqDto createUsersReqDto() {
        return UsersReqDto
                .builder()
                .loginId("kwang")
                .password("!work1234")
                .confirmPassword("!work1234")
                .userName("최광섭")
                .nickname("최광섭")
                .workspaceName("최광섭")
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

    public Users createUsersEntity() {
        Role role = roleRepository.findByRoleType(RoleType.ROLE_ARTIST)
                .orElseThrow(() -> new EntityNotFoundException(Role.class, null));
        UsersReqDto usersReqDto = createUsersReqDto();

        Users user = Users.create(usersReqDto, passwordEncoder.encode(usersReqDto.password()), role);

        usersRepository.save(user);

        return user;
    }
}
