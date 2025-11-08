package com.bantads.msconta.conta.mapper;

import com.bantads.msconta.conta.dto.DadoConta;
import com.bantads.msconta.conta.dto.OperacaoResponse;
import com.bantads.msconta.conta.dto.SaldoResponse;
import com.bantads.msconta.conta.query.model.ContaView;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContaViewMapper {

    public static SaldoResponse toSaldoResponse(ContaView conta){
        return new ModelMapper().map(conta, SaldoResponse.class);
    }

    public static OperacaoResponse toOperacaoResponse(ContaView conta){
        return new ModelMapper().map(conta, OperacaoResponse.class);
    }

    public static DadoConta toDadoConta(ContaView contaView){
        return new ModelMapper().map(contaView, DadoConta.class);
    }

}
