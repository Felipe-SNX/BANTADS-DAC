package com.bantads.msorquestrador.core.saga;

import com.bantads.msorquestrador.core.enums.ETopics;
import com.bantads.msorquestrador.core.model.Evento;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SagaStep {
    private final ETopics proximoTopico;
    private final Evento eventoParaEnviar;
}

