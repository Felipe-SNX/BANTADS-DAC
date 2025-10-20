package com.bantads.msconta.core.dto.mapper;

import com.bantads.msconta.core.dto.OperacaoResponse;
import com.bantads.msconta.core.dto.SaldoResponse;
import com.bantads.msconta.core.model.ContaView;
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

}
