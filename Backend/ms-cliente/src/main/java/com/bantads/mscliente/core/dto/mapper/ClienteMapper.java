package com.bantads.mscliente.core.dto.mapper;

import com.bantads.mscliente.core.dto.ClienteParaAprovarResponse;
import com.bantads.mscliente.core.model.Cliente;
import lombok.NoArgsConstructor;

import org.modelmapper.ModelMapper;


import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteMapper {

    public static ClienteParaAprovarResponse toClienteParaAprovarResponse(Cliente cliente){
        return new ModelMapper().map(cliente, ClienteParaAprovarResponse.class);
    }


}

