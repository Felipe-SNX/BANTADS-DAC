package com.bantads.msorquestrador.core.saga;

import com.bantads.msorquestrador.core.dto.Evento;
import com.bantads.msorquestrador.core.enums.ETopics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SagaStep {
    private final ETopics proximoTopico;
    private final Evento eventoParaEnviar;
}

