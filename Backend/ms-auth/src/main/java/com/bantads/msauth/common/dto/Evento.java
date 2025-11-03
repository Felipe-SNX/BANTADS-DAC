package com.bantads.msauth.common.dto;

import com.bantads.msauth.common.enums.EEventSource;
import com.bantads.msauth.common.enums.ESaga;
import com.bantads.msauth.common.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Evento {

    private String id;
    private String payload;
    private ESaga saga;
    private EEventSource source;
    private ESagaStatus status;
    private List<Historico> eventoHistorico;
    private LocalDateTime createdAt;

}