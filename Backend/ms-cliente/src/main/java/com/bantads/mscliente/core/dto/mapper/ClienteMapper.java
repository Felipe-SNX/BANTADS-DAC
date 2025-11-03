package com.bantads.mscliente.core.dto.mapper;

import com.bantads.mscliente.core.dto.AutoCadastroInfo;
import com.bantads.mscliente.core.dto.ClienteDto;
import com.bantads.mscliente.core.dto.ClienteParaAprovarResponse;
import com.bantads.mscliente.core.dto.RelatorioClientesResponse;
import com.bantads.mscliente.core.model.Cliente;
import lombok.NoArgsConstructor;

import org.modelmapper.ModelMapper;


import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteMapper {

    public static ClienteParaAprovarResponse toClienteParaAprovarResponse(Cliente cliente){
        return new ModelMapper().map(cliente, ClienteParaAprovarResponse.class);
    }

    public static Cliente autoCadastroInfoToCliente(AutoCadastroInfo autoCadastroInfo){
        return new ModelMapper().map(autoCadastroInfo, Cliente.class);
    }

    public static RelatorioClientesResponse clienteToRelatorioClientesResponse(Cliente cliente){
        return new ModelMapper().map(cliente, RelatorioClientesResponse.class);
    }

    public static ClienteDto toClienteDto(Cliente cliente){
        return new ModelMapper().map(cliente, ClienteDto.class);
    }

}

