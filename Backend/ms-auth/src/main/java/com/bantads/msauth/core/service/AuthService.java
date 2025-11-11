package com.bantads.msauth.core.service;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.dto.DadoGerenteAtualizacao;
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
import org.springframework.transaction.annotation.Transactional;

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

        Usuario usuarioLogado = this.buscarPorLogin(email);

        return new LogoutResponse(usuarioLogado.getCpf(), usuarioLogado.getEmail(), usuarioLogado.getTipoUsuario());
    }

    public Usuario buscarPorLogin(String login) {
        return authRepository.findByEmail(login)
            .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario com '%s' não encontrado", login)));
    }

    @Transactional
    public void cadastrarUsuarioCliente(DadosClienteConta dadosClienteConta) {
        authRepository.save(mapFrom(dadosClienteConta));
    }

    @Transactional
    public void cadastrarUsuarioGerente(DadoGerenteInsercao dadoGerenteInsercao) {
        authRepository.save(mapFrom(dadoGerenteInsercao));
    }

    @Transactional
    public void cadastrarUsuarioExcluido(Usuario user) {
        authRepository.save(mapFrom(user));
    }

    @Transactional
    public Usuario excluirUsuario(String cpf) {
        Optional<Usuario> usuarioOpt = authRepository.findByCpf(cpf);

        if (usuarioOpt.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOpt.get();
        authRepository.delete(usuario);
        return usuario;
    }

    @Transactional
    public void enviarEmailAprovado(DadosClienteConta dadosClienteConta) {
        Usuario usuario = this.buscarPorLogin(dadosClienteConta.getEmail());

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

    public void atualizarSenha(DadoGerenteAtualizacao dadoGerenteAtualizacao, String cpf){
        Usuario usuario = authRepository.findByCpf(cpf)
            .orElseThrow(() -> new UsuarioNotFoundException(String.format("Usuario com cpf '%s' não encontrado", cpf)));
        
        usuario.setSenha(passwordEncoder.encode(dadoGerenteAtualizacao.getSenha()));
        usuario.setEmail(dadoGerenteAtualizacao.getEmail());

        authRepository.save(usuario);
    }

    private Usuario mapFrom(DadosClienteConta dto) {
        return Usuario.builder()
            .email(dto.getEmail())
            .senha(null) 
            .cpf(dto.getCliente())
            .tipoUsuario(TipoUsuario.CLIENTE)
            .build();
    }

    private Usuario mapFrom(DadoGerenteInsercao dto) {
        return Usuario.builder()
            .email(dto.getEmail())
            .senha(passwordEncoder.encode(dto.getSenha()))
            .cpf(dto.getCpf())
            .tipoUsuario(TipoUsuario.GERENTE)
            .build();
    }

    private Usuario mapFrom(Usuario dto) {
        return Usuario.builder()
            .email(dto.getEmail())
            .senha(dto.getSenha()) 
            .cpf(dto.getCpf())
            .tipoUsuario(TipoUsuario.GERENTE) 
            .build();
    }
}