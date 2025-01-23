package org.example.workspace.factory;

import org.example.workspace.dto.request.*;
import org.example.workspace.entity.*;
import org.example.workspace.entity.code.MenuType;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.entity.code.SnsType;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.repository.*;
import org.example.workspace.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
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

    @Autowired
    private ContentsRepository contentsRepository;
    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Autowired
    private UserMenuRepository userMenuRepository;

    public UserCreateReqDto createUserCreateReqDto(String loginId, String workspaceName, String email) {
        return UserCreateReqDto
                .builder()
                .loginId(loginId)
                .password("!work1234")
                .confirmPassword("!work1234")
                .userName("최광섭")
                .nickname("최광섭")
                .workspaceName(workspaceName)
                .email(email)
                .phoneNumber("01012341234")
                .userSnsList(
                        List.of(
                                createUsersSnsReqDto(null, SnsType.FACEBOOK)
                        )
                )
                .build();
    }


    public UserUpdateReqDto createUserUpdateReqDto(UserSns usersSns, Long logoId) {
        Long id = usersSns != null ? usersSns.getId() : null;
        return UserUpdateReqDto.builder()
                .userName("updatename")
                .nickname("updatenickname")
                .workspaceName("updateworkspaceName")
                .email("update@gmail.com")
                .phoneNumber("01112341234")
                .bio("자기 소개글")
                .userSnsList(
                        List.of(
                                createUsersSnsReqDto(id, SnsType.INSTAGRAM),
                                createUsersSnsReqDto(null, SnsType.INSTAGRAM)
                        )
                )
                .logoId(logoId)
                .build();
    }

    public UsersSnsReqDto createUsersSnsReqDto(Long id, SnsType snsType) {
        return UsersSnsReqDto.builder()
                .id(id)
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

    public User createUsersEntity(String loginId, String workspaceName, String email) {
        Role role = roleRepository.findByRoleType(RoleType.ROLE_ARTIST)
                .orElseThrow(() -> new EntityNotFoundException(Role.class, null));
        UserCreateReqDto userCreateReqDto = createUserCreateReqDto(loginId, workspaceName, email);

        User user = User.create(userCreateReqDto, passwordEncoder.encode(userCreateReqDto.password()), role);

        userRepository.save(user);

        return user;
    }

    public UserSns createUsersSnsEntity(User user, SnsType snsType) {
        UsersSnsReqDto usersSnsReqDto = createUsersSnsReqDto(null, snsType);
        UserSns userSns = UserSns.create(user, usersSnsReqDto);
        usersSnsRepository.save(userSns);
        return userSns;
    }

    public Contents createContentEntity() {
        Contents contents = Contents.create(
                "211551_0c3bf720-95b2-40df-995b-bd66e95223c5.jpg",
                "test.jpg",
                "C:\\workspace\\upload\\2025\\01",
                151960L,
                MediaType.IMAGE_JPEG_VALUE
        );
        contentsRepository.save(contents);
        return contents;
    }

    public MockMultipartFile createMultipartFile(String filename, MediaType mediaType) {
        byte[] dummyImage = new byte[1024];
        Arrays.fill(dummyImage, (byte) 1);
        return new MockMultipartFile("file", filename, mediaType.getType(), dummyImage);
    }

    public UserPasswordReqDto createUserPasswordReqDto(String token) {
        return new UserPasswordReqDto(
                "!work1234",
                "!work1234",
                token
        );
    }

    public UserVerification createUserVerificationEntity(User user) {
        UserVerification userVerification = UserVerification.create(user);
        userVerificationRepository.save(userVerification);
        return userVerification;
    }

    public UserMenu createUserMenu(User user, Contents contents, MenuType menuType) {
        UserMenu userMenu = UserMenu.create(user, contents, menuType);
        userMenuRepository.save(userMenu);
        return userMenu;
    }
}
