package com.userservice.user.controller;

import com.userservice.common.dto.ResponseDTO;
import com.userservice.user.dto.request.LoginRequestDTO;
import com.userservice.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> signUpAdmin(
        @Valid @RequestBody LoginRequestDTO requestDTO) throws Exception {
        ResponseDTO<Void> response = adminService.signUpAdmin(requestDTO);
        return ResponseEntity.ok(response);
    }
}
