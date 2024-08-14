package com.firstcomestore.domain.user.service;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.common.jwt.JwtProvider;
import com.firstcomestore.common.util.AESUtil;
import com.firstcomestore.domain.user.dto.request.CreateUserRequestDTO;
import com.firstcomestore.domain.user.dto.request.TokenDTO;
import com.firstcomestore.domain.user.entity.User;
import com.firstcomestore.domain.user.entity.UserRole;
import com.firstcomestore.domain.user.exception.DuplicatedEmailException;
import com.firstcomestore.domain.user.exception.InvalidLoginException;
import com.firstcomestore.domain.user.exception.UserNotFoundException;
import com.firstcomestore.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public ResponseDTO<Object> signUp(CreateUserRequestDTO requestDTO) throws Exception {

        String encryptedEmail = AESUtil.encrypt(requestDTO.email());
        User existingUser = userRepository.findByEmail(encryptedEmail).orElse(null);

        if (existingUser != null) {
            if (existingUser.isDeleted() && isWithinOneMonth(existingUser.getDeletedAt())) {
                restoreUser(existingUser, requestDTO.password());
                return ResponseDTO.okWithMessageAndData("계정이 복구되었습니다.", null);
            } else if (!existingUser.isDeleted()) {
                throw new DuplicatedEmailException();
            }
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.password());
        User newUser = createUser(requestDTO, encodedPassword);
        userRepository.save(newUser);

        return ResponseDTO.okWithMessageAndData("회원가입이 완료되었습니다.", null);
    }

    private User createUser(CreateUserRequestDTO requestDTO, String encodedPassword)
        throws Exception {
        return User.builder()
            .email(AESUtil.encrypt(requestDTO.email()))
            .password(encodedPassword)
            .name(AESUtil.encrypt(requestDTO.name()))
            .phone(AESUtil.encrypt(requestDTO.phone()))
            .address(AESUtil.encrypt(requestDTO.address()))
            .userRole(UserRole.USER)
            .build();
    }

    private boolean isWithinOneMonth(LocalDateTime deletedAt) {
        return deletedAt != null && deletedAt.isAfter(LocalDateTime.now().minusMonths(1));
    }

    private void restoreUser(User user, String rawPassword) {
        user.restore();
        user.updatePassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public TokenDTO login(String email, String password) throws Exception {
        String encryptedEmail = AESUtil.encrypt(email);
        User user = findByEmail(encryptedEmail);
        validatePassword(password, user.getPassword());
        String accessToken = jwtProvider.createToken(user);
        return new TokenDTO(accessToken);
    }

    public void deleteUser(Long userId) {
        User user = findById(userId);
        markUserAsDeleted(user);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String encryptedEmail) {
        return userRepository.findByEmail(encryptedEmail)
            .orElseThrow(UserNotFoundException::new);
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidLoginException();
        }
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    private void markUserAsDeleted(User user) {
        user.delete(LocalDateTime.now());
    }
}
