package com.geekhub.security;

import java.util.HashSet;
import java.util.Set;

import com.geekhub.domain.entities.Authority;
import com.geekhub.domain.CustomUserDetail;
import com.geekhub.domain.entities.User;
import com.geekhub.repositories.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    public UserDetails loadUserByUsername(String name) throws UserNotFoundException {
        User domainUser = userRepository.findByUsername(name);
        Authority authority = domainUser.getAuthority();
        String role = authority.getRole();
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role));
        CustomUserDetail customUserDetail = new CustomUserDetail();
        customUserDetail.setUser(domainUser);
        customUserDetail.setAuthorities(authorities);
        return customUserDetail;
    }
}