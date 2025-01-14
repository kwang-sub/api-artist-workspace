package org.example.workspace.security;

import lombok.RequiredArgsConstructor;
import org.example.workspace.entity.Users;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByLoginIdAndIsDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        RoleType roleType = user.getRole().getRoleType();

        return CustomUserDetails.create(user, roleType);
    }
}
