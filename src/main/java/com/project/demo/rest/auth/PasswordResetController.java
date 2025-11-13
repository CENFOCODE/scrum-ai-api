package com.project.demo.rest.auth;

import com.project.demo.logic.utils.email.EmailService;
import com.project.demo.logic.entity.auth.PasswordResetService;
import com.project.demo.logic.entity.auth.RecoverPasswordRequest;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {


    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public PasswordResetController(PasswordResetService passwordResetService, EmailService emailService) {
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordRequest requestBody, HttpServletRequest request) {
        String email = requestBody.getEmail();

        Optional<String> tempPassword = passwordResetService.resetPassword(email);

        if (tempPassword.isPresent()) {
            emailService.sendTemporaryPassword(email, tempPassword.get());
        }

        return new GlobalResponseHandler().handleResponse(
                "Si el correo electrónico existe en nuestro sistema, se ha enviado una contraseña temporal.",
                HttpStatus.OK,
                request
        );
    }
}
