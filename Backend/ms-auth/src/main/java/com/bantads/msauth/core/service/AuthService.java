package com.bantads.msauth.core.service;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.enums.TipoUsuario;
import com.bantads.msauth.core.exception.UsuarioNotFoundException;
import com.bantads.msauth.core.repository.AuthRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    
    private final AuthRepository authRepository;
    
    public Usuario buscarPorLogin(String login) {
        return authRepository.findByLogin(login)
            .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario com '%s' não encontrado", login)));
    }
    
}
