package com.userservice.user.service;

import com.userservice.common.util.AESUtil;
import com.userservice.user.dto.request.UpdatePasswordRequestDTO;
import com.userservice.user.dto.request.UpdateUserRequestDTO;
import com.userservice.user.dto.response.UserResponseDTO;
import com.userservice.user.entity.User;
import com.userservice.user.exception.DuplicatedEmailException;
import com.userservice.user.exception.InvalidLoginException;
import com.userservice.user.exception.UserNotFoundException;
import com.userservice.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO getUserDetails(Long userId) throws Exception {
        User user = findById(userId);

        return UserResponseDTO.builder()
            .email(AESUtil.decrypt(user.getEmail()))
            .name(AESUtil.decrypt(user.getName()))
            .phone(AESUtil.decrypt(user.getPhone()))
            .address(AESUtil.decrypt(user.getAddress()))
            .build();
    }

    public void updateUserDetails(Long userId, UpdateUserRequestDTO updateUserRequestDTO)
        throws Exception {
        User user = findById(userId);

        Optional<String> newEmail = Optional.ofNullable(updateUserRequestDTO.email())
            .map(AESUtil::encryptSafely);

        if (newEmail.isPresent() && !newEmail.get().equals(user.getEmail())) {
            boolean emailExists = userRepository.findByEmail(newEmail.get()).isPresent();
            if (emailExists) {
                throw new DuplicatedEmailException();
            }
        }

        user.updateUserDetails(
            Optional.ofNullable(updateUserRequestDTO.name()).map(AESUtil::encryptSafely),
            Optional.ofNullable(updateUserRequestDTO.phone()).map(AESUtil::encryptSafely),
            newEmail,
            Optional.ofNullable(updateUserRequestDTO.address()).map(AESUtil::encryptSafely)
        );

        userRepository.save(user);
    }

    public void updatePassword(Long userId, UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        User user = findById(userId);

        if (!passwordEncoder.matches(updatePasswordRequestDTO.currentPassword(),
            user.getPassword())) {
            throw new InvalidLoginException();
        }
        String encodedNewPassword = passwordEncoder.encode(updatePasswordRequestDTO.newPassword());
        user.updatePassword(encodedNewPassword);
        userRepository.save(user);
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }
}
