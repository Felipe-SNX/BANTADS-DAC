package com.bantads.msconta.conta.mapper;

import com.bantads.msconta.conta.command.model.Conta;
import com.bantads.msconta.conta.dto.OperacaoResponse;
import com.bantads.msconta.conta.dto.SaldoResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContaMapper {

    public static SaldoResponse toSaldoResponse(Conta conta){
        return new ModelMapper().map(conta, SaldoResponse.class);
    }

    public static OperacaoResponse toOperacaoResponse(Conta conta){
        return new ModelMapper().map(conta, OperacaoResponse.class);
    }

}
