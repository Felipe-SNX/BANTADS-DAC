package com.bantads.msauth.core.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.enums.TipoUsuario;
import com.bantads.msauth.core.repository.AuthRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder; 

    @Transactional 
    public void popularBanco() {
        log.info("Zerando banco de dados de ms-auth");
        authRepository.deleteAll();

        Usuario auth1 = Usuario.builder()
            .tipoUsuario(TipoUsuario.CLIENTE)
            .email("cli1@bantads.com.br")
            .senha(passwordEncoder.encode("tads")) 
            .cpf("12912861012")
            .ativo(true)
            .build();

        Usuario auth2 = Usuario.builder()
            .tipoUsuario(TipoUsuario.CLIENTE)
            .email("cli2@bantads.com.br")
            .senha(passwordEncoder.encode("tads")) 
            .cpf("09506382000")
            .ativo(true)
            .build();

        Usuario auth3 = Usuario.builder()
            .tipoUsuario(TipoUsuario.CLIENTE)
            .email("cli3@bantads.com.br")
            .senha(passwordEncoder.encode("tads")) 
            .cpf("85733854057")
            .ativo(true)
            .build();

        Usuario auth4 = Usuario.builder()
            .tipoUsuario(TipoUsuario.CLIENTE)
            .email("cli4@bantads.com.br")
            .senha(passwordEncoder.encode("tads"))
            .cpf("58872160006")
            .ativo(true)
            .build();

        Usuario auth5 = Usuario.builder()
            .tipoUsuario(TipoUsuario.CLIENTE)
            .email("cli5@bantads.com.br")
            .senha(passwordEncoder.encode("tads")) 
            .cpf("76179646090")
            .ativo(true)
            .build();

        Usuario auth6 = Usuario.builder()
            .tipoUsuario(TipoUsuario.GERENTE)
            .email("ger1@bantads.com.br")
            .senha(passwordEncoder.encode("tads")) 
            .cpf("98574307084")
            .ativo(true)
            .build();

        Usuario auth7 = Usuario.builder()
            .tipoUsuario(TipoUsuario.GERENTE)
            .email("ger2@bantads.com.br")
            .senha(passwordEncoder.encode("tads")) 
            .cpf("64065268052")
            .ativo(true)
            .build();

        Usuario auth8 = Usuario.builder()
            .tipoUsuario(TipoUsuario.GERENTE)
            .email("ger3@bantads.com.br")
            .senha(passwordEncoder.encode("tads")) 
            .cpf("23862179060")
            .ativo(true)
            .build();

        Usuario auth9 = Usuario.builder()
            .tipoUsuario(TipoUsuario.ADMINISTRADOR)
            .email("adm1@bantads.com.br")
            .senha(passwordEncoder.encode("tads"))
            .cpf("40501740066")
            .ativo(true)
            .build();

        List<Usuario> usuarios = List.of(
            auth1, auth2, auth3, auth4, auth5, auth6, auth7, auth8, auth9
        );

        authRepository.saveAll(usuarios);
        log.info("banco de dados de ms-auth resetado");
    }
}
