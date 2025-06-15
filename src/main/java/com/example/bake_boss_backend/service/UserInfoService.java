package com.example.bake_boss_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.entity.UserInfo;
import com.example.bake_boss_backend.repository.UserInfoRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class UserInfoService {
    private final UserInfoRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserInfoService(UserInfoRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void initializeAdminUser() {
        boolean adminExists = repository.existsByRoles("ROLE_ADMIN");
        if (!adminExists) {
            UserInfo adminUser = new UserInfo();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("485686@farid"));
            adminUser.setRoles("ROLE_ADMIN");
            repository.save(adminUser);
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        String encryptedPassword = passwordEncoder.encode(newPassword);
        int rowsAffected = repository.updatePasswordByUsername(username, encryptedPassword);
        return rowsAffected > 0;
    }
}
