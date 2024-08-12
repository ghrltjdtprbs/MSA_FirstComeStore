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

    public ResponseDTO<Void> signUp(CreateUserRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new DuplicatedEmailException();
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.password());

        User user = User.builder()
            .email(requestDTO.email())
            .password(encodedPassword)
            .name(requestDTO.name())
            .phone(requestDTO.phone())
            .address(requestDTO.address())
            .userRole(UserRole.USER)
            .build();

        userRepository.save(user);

        return ResponseDTO.ok();
    }


    public TokenDTO login(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidLoginException();
        }
        String accessToken = jwtProvider.createToken(user);
        return new TokenDTO(accessToken);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());
        user.delete(LocalDateTime.now());
        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(UserNotFoundException::new);
        isDeletedUser(user);
        return user;
    }

    private static void isDeletedUser(User user) {
        if (user.isDeleted()) {
            throw new UserNotFoundException();
        }
    }


}
