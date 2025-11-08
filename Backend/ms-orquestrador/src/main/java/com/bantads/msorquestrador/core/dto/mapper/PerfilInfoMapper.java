package com.bantads.msorquestrador.core.dto.mapper;

import com.bantads.msorquestrador.core.dto.PerfilInfo;
import com.bantads.msorquestrador.core.dto.PerfilInfoResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PerfilInfoMapper {

    public static PerfilInfoResponse toPerfilInfoResponse(PerfilInfo perfilInfo){
        return new ModelMapper().map(perfilInfo, PerfilInfoResponse.class);
    }
}
