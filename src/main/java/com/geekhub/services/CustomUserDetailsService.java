package com.geekhub.services;

import java.util.HashSet;
import java.util.Set;

import com.geekhub.domain.CustomUserDetails;
import com.geekhub.domain.UserRole;
import com.geekhub.domain.entities.Authority;
import com.geekhub.domain.entities.User;
import com.geekhub.repositories.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.geekhub.exceptions.UserNotFoundException;

@Service
@Qualifier("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UserNotFoundException {
        try {
            User domainUser = userRepository.findByUsername(name);
            Authority authority = domainUser.getAuthority();
            UserRole role = authority.getRole();
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority(role.toString()));
            return new CustomUserDetails(domainUser, authorities);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails myUser = (CustomUserDetails) authentication.getPrincipal();
        return myUser.getUser();
    }
}