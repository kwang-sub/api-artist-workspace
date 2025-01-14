package org.example.workspace.factory;

import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.entity.code.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class ObjectFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetails createRoleUserDetails(AuthReqDto authReqDto) {
        return User.builder()
                .username(authReqDto.username())
                .password(passwordEncoder.encode(authReqDto.password()))
                .authorities(List.of(new SimpleGrantedAuthority(RoleName.ROLE_ARTIST.name())))
                .build();
    }
}
