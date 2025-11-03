package com.bantads.msauth.core.jwt;

import com.bantads.msauth.core.document.Usuario;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {

    private Usuario usuario;

    public JwtUserDetails(Usuario usuario) {
        super(usuario.getLogin(), usuario.getSenha(), AuthorityUtils.createAuthorityList(usuario.getTipoUsuario().name()));
        this.usuario = usuario;
    }

    public Long getId() {
        return this.usuario.getIdUsuario();
    }

    public String getRole() {
        return this.usuario.getTipoUsuario().name();
    }
}
