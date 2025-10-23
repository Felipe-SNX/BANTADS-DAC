package com.bantads.msgerente.core.dto.mapper;

import lombok.NoArgsConstructor;

import org.modelmapper.ModelMapper;

import com.bantads.msgerente.core.dto.DadoGerente;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.GerentesResponse;
import com.bantads.msgerente.core.model.Gerente;

import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GerenteMapper {

    public static GerentesResponse toGerentesResponse(Gerente gerente){
        return new ModelMapper().map(gerente, GerentesResponse.class);
    }

    public static Gerente dadoGerenteInsercaoToGerente(DadoGerenteInsercao dadoGerenteInsercao){
        return new ModelMapper().map(dadoGerenteInsercao, Gerente.class);
    }

    public static DadoGerente toDadoGerente(Gerente gerente){
        return new ModelMapper().map(gerente, DadoGerente.class);
    }

}
