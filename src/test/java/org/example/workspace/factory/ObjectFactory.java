package org.example.workspace.factory;

import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.dto.request.UserReqDto;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.entity.Role;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserSns;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.entity.code.SnsType;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.repository.RoleRepository;
import org.example.workspace.repository.UserRepository;
import org.example.workspace.repository.UsersSnsRepository;
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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsersSnsRepository usersSnsRepository;

    public UserReqDto createUsersReqDto() {
        return UserReqDto
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
        User user = User.builder()
                .loginId(authReqDto.username())
                .password(passwordEncoder.encode(authReqDto.password()))
                .role(createRole(RoleType.ROLE_ARTIST))
                .isActivated(true)
                .build();

        return CustomUserDetails.create(user, user.getRole().getRoleType());
    }

    public User createUsersEntity() {
        Role role = roleRepository.findByRoleType(RoleType.ROLE_ARTIST)
                .orElseThrow(() -> new EntityNotFoundException(Role.class, null));
        UserReqDto userReqDto = createUsersReqDto();

        User user = User.create(userReqDto, passwordEncoder.encode(userReqDto.password()), role);

        userRepository.save(user);

        return user;
    }

    public UserSns createUsersSns(User user) {
        UsersSnsReqDto usersSnsReqDto = createUsersSnsReqDto(SnsType.INSTAGRAM);
        UserSns userSns = UserSns.create(user, usersSnsReqDto);
        usersSnsRepository.save(userSns);
        return userSns;
    }
}
