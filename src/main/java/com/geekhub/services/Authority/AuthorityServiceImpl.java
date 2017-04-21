package com.geekhub.services.Authority;

import com.geekhub.domain.UserRole;
import com.geekhub.domain.entities.Authority;
import com.geekhub.repositories.Authority.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private AuthorityRepository authorityRepository;

    @Autowired
    public void setAuthorityRepository(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    @Override
    public Authority findOne(Long id) {
        return authorityRepository.findOne(id);
    }

    @Override
    public Authority findByRole(UserRole role) {
        return authorityRepository.findByRole(role);
    }
}