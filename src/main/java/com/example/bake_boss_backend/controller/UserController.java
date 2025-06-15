package com.example.bake_boss_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bake_boss_backend.entity.UserInfo;
import com.example.bake_boss_backend.repository.UserInfoRepository;
import com.example.bake_boss_backend.service.UserInfoService;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/addNewUser")
    public ResponseEntity<?> addNewUser(@RequestBody UserInfo userInfo) {
        if (userInfoRepository.existsByUsername(userInfo.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username " + userInfo.getUsername() + " already exists!");
        }
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        UserInfo savedUser = userInfoRepository.save(userInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/userChange")
    public ResponseEntity<String> updatePassword(@RequestParam String username, @RequestParam String newPassword) {
        boolean isUpdated = userInfoService.updatePassword(username, newPassword);
        if (isUpdated) {
            return ResponseEntity.ok("Password updated successfully.");
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }

    @GetMapping("/userLogin")
    public ResponseEntity<Map<String, String>> getUserInfo(@RequestParam String username,
            @RequestParam String password) {
        UserInfo user = userInfoRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User found");
            response.put("roles", user.getRoles());
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(error);
        }
    }

    @GetMapping("/user/userList")
    public List<UserInfo> getUsers() {
        return userInfoRepository.findAll();
    }

    @GetMapping("/user/getSalesUser")
    public List<UserInfo> getSales(String roles) {
        return userInfoRepository.findByRoles("ROLE_SALES");
    }
}
