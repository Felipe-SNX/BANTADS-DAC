package com.bantads.msconta.core.dto.mapper;

import com.bantads.msconta.core.dto.SaldoResponse;
import com.bantads.msconta.core.model.Conta;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContaMapper {

    public static SaldoResponse toSaldoResponse(Conta conta){
        return new ModelMapper().map(conta, SaldoResponse.class);
    }

}
