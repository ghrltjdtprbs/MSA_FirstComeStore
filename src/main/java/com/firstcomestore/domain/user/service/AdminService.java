package com.firstcomestore.domain.user.service;

import com.firstcomestore.common.dto.ResponseDTO;
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

    public ResponseDTO<Void> signUpAdmin(LoginRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new DuplicatedEmailException();
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.password());
        User newUser = createAdminUser(requestDTO, encodedPassword);
        userRepository.save(newUser);

        return ResponseDTO.ok();
    }

    private User createAdminUser(LoginRequestDTO requestDTO, String encodedPassword) {
        return User.builder()
            .email(requestDTO.email())
            .password(encodedPassword)
            .userRole(UserRole.ADMIN)
            .build();
    }
}
