package com.bantads.msauth.core.controller;

import com.bantads.msauth.config.exception.ErrorMessage;
import com.bantads.msauth.core.dto.LoginInfo;
import com.bantads.msauth.core.dto.LoginResponseDto;
import com.bantads.msauth.core.dto.LogoutResponse;
import com.bantads.msauth.core.jwt.JwtUserDetailsService;
import com.bantads.msauth.core.service.AuthService;
import com.bantads.msauth.core.service.DataService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final DataService dataService;

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        dataService.popularBanco();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@RequestBody LoginInfo dto, HttpServletRequest request) {
        log.info("Processo de autenticação pelo login {}", dto.getLogin());
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(dto.getLogin(), dto.getSenha());

            authenticationManager.authenticate(authenticationToken);
            LoginResponseDto response = detailsService.buildLoginResponse(dto.getLogin());

            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            log.warn("Bad Credentials from username '{}'", dto.getLogin());
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(request, HttpStatus.UNAUTHORIZED, "Credenciais Inválidas"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        LogoutResponse logoutResponse = authService.logout();
        return ResponseEntity.ok(logoutResponse);
    }
}