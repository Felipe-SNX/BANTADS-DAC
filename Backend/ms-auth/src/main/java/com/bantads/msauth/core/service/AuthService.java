package com.bantads.msauth.core.service;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.dto.AutoCadastroInfo;
import com.bantads.msauth.core.enums.TipoUsuario;
import com.bantads.msauth.core.exception.UsuarioNotFoundException;
import com.bantads.msauth.core.repository.AuthRepository;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Usuario buscarPorLogin(String login) {
        return authRepository.findByLogin(login)
            .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario com '%s' não encontrado", login)));
    }

    public void cadastrarUsuario(AutoCadastroInfo autoCadastroInfo){
        var usuario = Usuario
            .builder()
            .login(autoCadastroInfo.getEmail())
            .senha(gerarSenha())
            .cpfUsuario(autoCadastroInfo.getCpf())
            .tipoUsuario(TipoUsuario.CLIENTE)
            .build();
        
        authRepository.save(usuario);
    }

    private String gerarSenha(){
        String senhaPura = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        //Aqui é necessário enviar a senha original para o cliente
        return passwordEncoder.encode(senhaPura);
    }

}
