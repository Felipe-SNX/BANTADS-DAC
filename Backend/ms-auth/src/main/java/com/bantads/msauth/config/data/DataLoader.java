package com.bantads.msauth.config.data;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.enums.TipoUsuario;
import com.bantads.msauth.core.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final AuthRepository authRepository;

    @Override
    public void run(String... args) throws Exception {

        if (authRepository.count() == 0) {
            System.out.println(">>> Semeando dados iniciais de Usuários...");

            Usuario auth1 = Usuario.builder()
                    .idUsuario(1L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli1@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(1L)
                    .build();

            Usuario auth2 = Usuario.builder()
                    .idUsuario(2L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli2@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(2L)
                    .build();

            Usuario auth3 = Usuario.builder()
                    .idUsuario(3L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli3@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(3L)
                    .build();

            Usuario auth4 = Usuario.builder()
                    .idUsuario(4L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli4@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(4L)
                    .build();

            Usuario auth5 = Usuario.builder()
                    .idUsuario(5L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli5@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(5L)
                    .build();

            Usuario auth6 = Usuario.builder()
                    .idUsuario(6L)
                    .tipoUsuario(TipoUsuario.GERENTE)
                    .login("ger1@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(1L)
                    .build();

            Usuario auth7 = Usuario.builder()
                    .idUsuario(7L)
                    .tipoUsuario(TipoUsuario.GERENTE)
                    .login("ger2@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(2L)
                    .build();

            Usuario auth8 = Usuario.builder()
                    .idUsuario(8L)
                    .tipoUsuario(TipoUsuario.GERENTE)
                    .login("ger3@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(3L)
                    .build();

            Usuario auth9 = Usuario.builder()
                    .idUsuario(9L)
                    .tipoUsuario(TipoUsuario.ADMIN)
                    .login("adm1@bantads.com.br")
                    .senha("tads")
                    .idClienteGerente(4L)
                    .build();

            List<Usuario> usuarios = List.of(
                    auth1, auth2, auth3, auth4, auth5, auth6, auth7, auth8, auth9
            );

            authRepository.saveAll(usuarios);
            System.out.println(">>> " + usuarios.size() + " usuários inseridos.");
        }
    }
}
