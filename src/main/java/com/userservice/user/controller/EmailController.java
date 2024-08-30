package com.userservice.user.controller;

import com.userservice.common.dto.ResponseDTO;
import com.userservice.user.dto.request.SendEmailRequestDTO;
import com.userservice.user.dto.request.VerifyEmailRequestDTO;
import com.userservice.user.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> mailConfirm(
        @Valid @RequestBody SendEmailRequestDTO emailRequest)
        throws Exception {
        emailService.sendVerificationEmail(emailRequest.email());
        return ResponseEntity.ok(
            ResponseDTO.okWithMessageAndData("인증 이메일이 성공적으로 전송되었습니다.", null)
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO<Boolean>> verifyEmail(
        @Valid @RequestBody VerifyEmailRequestDTO requestDTO) {

        boolean isVerified = emailService.verifyEmailCode(requestDTO.email(), requestDTO.code());
        if (isVerified) {
            return ResponseEntity.ok(ResponseDTO.okWithMessageAndData("이메일 인증 성공.", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ResponseDTO.errorWithMessageAndData(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다.",
                        false));
        }
    }
}
