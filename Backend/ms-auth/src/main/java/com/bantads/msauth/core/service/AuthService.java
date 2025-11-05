package com.bantads.msauth.core.service;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.dto.AutoCadastroInfo;
import com.bantads.msauth.core.dto.LogoutResponse;
import com.bantads.msauth.core.enums.TipoUsuario;
import com.bantads.msauth.core.exception.UsuarioNotFoundException;
import com.bantads.msauth.core.repository.AuthRepository;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public LogoutResponse logout() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = authentication.getName();
        }

        return new LogoutResponse(email);
    }

    public Usuario buscarPorLogin(String login) {
        return authRepository.findByEmail(login)
            .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario com '%s' não encontrado", login)));
    }

    public void cadastrarUsuario(AutoCadastroInfo autoCadastroInfo){
        var usuario = Usuario
            .builder()
            .email(autoCadastroInfo.getEmail())
            .senha(gerarSenha())
            .cpf(autoCadastroInfo.getCpf())
            .tipoUsuario(TipoUsuario.CLIENTE)
            .build();
        
        authRepository.save(usuario);
    }

    private String gerarSenha(){
        String senhaPura = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        log.info("Essa é a senha gerada: {}", senhaPura);
        //Aqui é necessário enviar a senha original para o cliente
        return passwordEncoder.encode(senhaPura);
    }

}
