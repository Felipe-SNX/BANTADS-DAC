package com.bantads.msconta.common.saga.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bantads.msconta.common.saga.enums.EEventSource;
import com.bantads.msconta.common.saga.enums.ESaga;
import com.bantads.msconta.common.saga.enums.ESagaStatus;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
