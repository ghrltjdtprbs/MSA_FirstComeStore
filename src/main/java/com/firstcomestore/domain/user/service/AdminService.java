package com.firstcomestore.domain.user.service;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.common.util.AESUtil;
import com.firstcomestore.domain.user.dto.request.LoginRequestDTO;
import com.firstcomestore.domain.user.entity.User;
import com.firstcomestore.domain.user.entity.UserRole;
import com.firstcomestore.domain.user.exception.DuplicatedEmailException;
import com.firstcomestore.domain.user.repository.UserRepository;
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
