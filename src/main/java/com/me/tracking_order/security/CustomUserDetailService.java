package com.me.tracking_order.security;


import com.me.tracking_order.entity.Role;

import com.me.tracking_order.entity.User;
import com.me.tracking_order.entity.UserRole;
import com.me.tracking_order.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));

        List<SimpleGrantedAuthority> authorities =
                user.getUserRoles()
                        .stream()
                        .filter(userRole -> !userRole.isDeleted())
                        .map(UserRole::getRole)
                        .filter(role -> !role.isDeleted())
                        .map(Role::getName)
                        .map(String::toUpperCase)
                        .map(roleName -> new SimpleGrantedAuthority("ROLE_"+roleName))
                        .toList();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(user.isDeleted())
                .build();
    }


}
