package com.bantads.msauth.core.jwt;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.dto.LoginResponseDto;
import com.bantads.msauth.core.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = authService.buscarPorLogin(login);
        
        return User
                .withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(usuario.getTipoUsuario().name()) 
                .build();
    }

    public LoginResponseDto buildLoginResponse(String login) {
        Usuario usuario = authService.buscarPorLogin(login);
        
        String role = usuario.getTipoUsuario().name();
        
        String token = jwtUtils.createToken(login, role).getToken();


        return LoginResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer") 
                .tipo(usuario.getTipoUsuario())
                .usuario(usuario) 
                .build();
    }
}


