package com.bantads.msauth.core.dto;

import com.bantads.msauth.core.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutResponse {

    private String cpf;
    private String nome;
    private String email;
    private TipoUsuario tipoUsuario;

    public LogoutResponse(String email){
        this.email = email;
    }
}
