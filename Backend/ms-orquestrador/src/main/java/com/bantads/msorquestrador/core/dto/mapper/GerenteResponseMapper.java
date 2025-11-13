package com.bantads.msorquestrador.core.dto.mapper;

import org.modelmapper.ModelMapper;

import com.bantads.msorquestrador.core.dto.DadoGerenteAtualizacao;
import com.bantads.msorquestrador.core.dto.GerentesResponse;
import lombok.AccessLevel;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GerenteResponseMapper {

    public static GerentesResponse toGerentesResponse(DadoGerenteAtualizacao dadoGerenteAtualizacao){
        return new ModelMapper().map(dadoGerenteAtualizacao, GerentesResponse.class);
    }
}
