package com.bantads.msconta.common.conta.mapper;

import com.bantads.msconta.common.conta.dto.ItemExtratoResponse;
import com.bantads.msconta.query.model.MovimentacaoView;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovimentacaoViewMapper {

    public static ItemExtratoResponse toItemExtratoResponse(MovimentacaoView movimentacaoView) {
        return new ModelMapper().map(movimentacaoView, ItemExtratoResponse.class);
    }
}
