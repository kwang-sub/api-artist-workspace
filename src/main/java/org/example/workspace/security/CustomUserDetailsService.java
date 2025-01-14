package org.example.workspace.security;

import lombok.RequiredArgsConstructor;
import org.example.workspace.entity.Users;
import org.example.workspace.repository.UsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByLoginIdAndIsDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // TODO UserDetails 구현 필요
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name()));
        return User.builder()
                .username(user.getLoginId())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
