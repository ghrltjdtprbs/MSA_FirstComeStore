package com.userservice.user.service;

import com.userservice.common.dto.ResponseDTO;
import com.userservice.common.util.AESUtil;
import com.userservice.user.dto.request.LoginRequestDTO;
import com.userservice.user.entity.User;
import com.userservice.user.entity.UserRole;
import com.userservice.user.exception.DuplicatedEmailException;
import com.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseDTO<Void> signUpAdmin(LoginRequestDTO requestDTO) throws Exception {
        String encryptedEmail = AESUtil.encrypt(requestDTO.email());

        if (userRepository.findByEmail(encryptedEmail).isPresent()) {
            throw new DuplicatedEmailException();
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.password());
        User newUser = createAdminUser(encryptedEmail, encodedPassword);
        userRepository.save(newUser);

        return ResponseDTO.ok();
    }

    private User createAdminUser(String encryptedEmail, String encodedPassword) {
        return User.builder()
            .email(encryptedEmail)
            .password(encodedPassword)
            .userRole(UserRole.ADMIN)
            .build();
    }
}
