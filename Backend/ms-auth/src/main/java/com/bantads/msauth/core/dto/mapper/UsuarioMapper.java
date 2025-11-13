package com.bantads.msauth.core.dto.mapper;

import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.dto.LoginInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UsuarioMapper {

    public static LoginInfo toLoginInfo(Usuario usuario){
        return new ModelMapper().map(usuario, LoginInfo.class);
    }
}
