package com.bantads.msgerente.common.dto;

import com.bantads.msgerente.common.enums.EEventSource;
import com.bantads.msgerente.common.enums.ESaga;
import com.bantads.msgerente.common.enums.ESagaStatus;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Evento {

    private String id;

    @Lob
    private String payload;

    private ESaga saga;
    private EEventSource source;
    private ESagaStatus status;
    private List<Historico> eventoHistorico;
    private LocalDateTime createdAt;

}



