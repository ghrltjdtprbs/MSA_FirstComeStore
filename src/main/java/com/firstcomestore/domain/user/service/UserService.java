package com.firstcomestore.domain.user.service;

import com.firstcomestore.common.dto.ResponseDTO;
import com.firstcomestore.common.jwt.JwtProvider;
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

    public ResponseDTO<Object> signUp(CreateUserRequestDTO requestDTO) {
        User existingUser = userRepository.findByEmail(requestDTO.email()).orElse(null);

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
        return null;
    }

    private boolean isWithinOneMonth(LocalDateTime deletedAt) {
        return deletedAt != null && deletedAt.isAfter(LocalDateTime.now().minusMonths(1));
    }

    private void restoreUser(User user, String rawPassword) {
        user.restore();
        user.updatePassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public TokenDTO login(String email, String password) {
        User user = findByEmail(email);
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
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);
        validateNotDeleted(user);
        return user;
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidLoginException();
        }
    }

    private User createUser(CreateUserRequestDTO requestDTO, String encodedPassword) {
        return User.builder()
            .email(requestDTO.email())
            .password(encodedPassword)
            .name(requestDTO.name())
            .phone(requestDTO.phone())
            .address(requestDTO.address())
            .userRole(UserRole.USER)
            .build();
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    private void markUserAsDeleted(User user) {
        user.delete(LocalDateTime.now());
    }

    private void validateNotDeleted(User user) {
        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }
    }
}
