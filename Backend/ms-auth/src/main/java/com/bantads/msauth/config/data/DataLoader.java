package com.bantads.msauth.config.data;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.enums.TipoUsuario;
import com.bantads.msauth.core.repository.AuthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder; 

    public DataLoader(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (authRepository.count() == 0) {
            System.out.println(">>> Semeando dados iniciais de Usuários...");

            Usuario auth1 = Usuario.builder()
                    .id(1L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli1@bantads.com.br")
                    .senha(passwordEncoder.encode("tads")) 
                    .cpfUsuario("12912861012")
                    .build();

            Usuario auth2 = Usuario.builder()
                    .id(2L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli2@bantads.com.br")
                    .senha(passwordEncoder.encode("tads")) 
                    .cpfUsuario("09506382000")
                    .build();

            Usuario auth3 = Usuario.builder()
                    .id(3L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli3@bantads.com.br")
                    .senha(passwordEncoder.encode("tads")) 
                    .cpfUsuario("85733854057")
                    .build();

            Usuario auth4 = Usuario.builder()
                    .id(4L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli4@bantads.com.br")
                    .senha(passwordEncoder.encode("tads"))
                    .cpfUsuario("58872160006")
                    .build();

            Usuario auth5 = Usuario.builder()
                    .id(5L)
                    .tipoUsuario(TipoUsuario.CLIENTE)
                    .login("cli5@bantads.com.br")
                    .senha(passwordEncoder.encode("tads")) 
                    .cpfUsuario("76179646090")
                    .build();

            Usuario auth6 = Usuario.builder()
                    .id(6L)
                    .tipoUsuario(TipoUsuario.GERENTE)
                    .login("ger1@bantads.com.br")
                    .senha(passwordEncoder.encode("tads")) 
                    .cpfUsuario("98574307084")
                    .build();

            Usuario auth7 = Usuario.builder()
                    .id(7L)
                    .tipoUsuario(TipoUsuario.GERENTE)
                    .login("ger2@bantads.com.br")
                    .senha(passwordEncoder.encode("tads")) 
                    .cpfUsuario("64065268052")
                    .build();

            Usuario auth8 = Usuario.builder()
                    .id(8L)
                    .tipoUsuario(TipoUsuario.GERENTE)
                    .login("ger3@bantads.com.br")
                    .senha(passwordEncoder.encode("tads")) 
                    .cpfUsuario("23862179060")
                    .build();

            Usuario auth9 = Usuario.builder()
                    .id(9L)
                    .tipoUsuario(TipoUsuario.ADMIN)
                    .login("adm1@bantads.com.br")
                    .senha(passwordEncoder.encode("tads"))
                    .cpfUsuario("40501740066")
                    .build();

            List<Usuario> usuarios = List.of(
                    auth1, auth2, auth3, auth4, auth5, auth6, auth7, auth8, auth9
            );

            authRepository.saveAll(usuarios);
            System.out.println(">>> " + usuarios.size() + " usuários inseridos.");
        }
    }
}