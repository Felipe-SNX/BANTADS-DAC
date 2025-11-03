package com.bantads.msauth.core.service;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.enums.TipoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    public Usuario buscarPorUsername(String username) {
    }

    public TipoUsuario buscarRolePorUsername(String username) {
    }
}
