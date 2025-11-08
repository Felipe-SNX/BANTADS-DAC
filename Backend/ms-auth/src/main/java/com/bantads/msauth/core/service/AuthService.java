package com.bantads.msauth.core.service;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.dto.AutoCadastroInfo;
import com.bantads.msauth.core.dto.DadoGerenteInsercao;
import com.bantads.msauth.core.dto.DadosClienteConta;
import com.bantads.msauth.core.dto.LogoutResponse;
import com.bantads.msauth.core.enums.TipoUsuario;
import com.bantads.msauth.core.exception.UsuarioNotFoundException;
import com.bantads.msauth.core.repository.AuthRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
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
    private final EmailService emailService;
    private final BlackListTokensService blackListTokensService;

    public LogoutResponse logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        String token = (String) authentication.getCredentials();

        if (token != null) {
            blackListTokensService.blacklistToken(token);
        }

        Optional<Usuario> usuarioPesquisa = authRepository.findByEmail(email);
        Usuario usuarioLogado = usuarioPesquisa.orElse(new Usuario());

        return new LogoutResponse(usuarioLogado.getCpf(), usuarioLogado.getEmail(), usuarioLogado.getTipoUsuario());
    }

    public Usuario buscarPorLogin(String login) {
        return authRepository.findByEmail(login)
            .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario com '%s' não encontrado", login)));
    }

    public void cadastrarUsuarioCliente(DadosClienteConta dadosClienteConta){
        var usuario = Usuario
            .builder()
            .email(dadosClienteConta.getEmail())
            .senha(null)
            .cpf(dadosClienteConta.getCliente())
            .tipoUsuario(TipoUsuario.CLIENTE)
            .build();
        
        authRepository.save(usuario);
    }

    public void cadastrarUsuarioGerente(DadoGerenteInsercao dadoGerenteInsercao){

        var usuario = Usuario
                .builder()
                .email(dadoGerenteInsercao.getEmail())
                .senha(passwordEncoder.encode(dadoGerenteInsercao.getSenha()))
                .cpf(dadoGerenteInsercao.getCpf())
                .tipoUsuario(TipoUsuario.GERENTE)
                .build();

        authRepository.save(usuario);
    }


    public void enviarEmailAprovado(DadosClienteConta dadosClienteConta){
        Usuario usuario = authRepository.findByCpf(dadosClienteConta.getCliente())
                .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario com '%s' não encontrado", dadosClienteConta.getCliente())));

        String senhaPura = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        usuario.setSenha(passwordEncoder.encode(senhaPura));
        authRepository.save(usuario);

        log.info("----------------------------------------------");
        log.info("Senha para email {} é {}", usuario.getEmail(), senhaPura);
        log.info("----------------------------------------------");

        String destinatario = usuario.getEmail();
        String assunto = "Aprovação Cadastro Internet Banking Bantads";
        String corpo = "Olá! \n\n" + "Seu cadastro no banco Bantads foi aprovado com sucesso.\n" + " A senha de acesso é " + senhaPura;

        emailService.enviarEmailAprovado(destinatario, assunto, corpo);
    }



}
