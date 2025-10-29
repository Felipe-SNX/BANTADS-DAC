package com.bantads.msorquestrador.core.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.bantads.msorquestrador.core.enums.EEventSource;
import com.bantads.msorquestrador.core.enums.ESagaStatus;

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
    
    private EEventSource source;
    private ESagaStatus status;
    private List<Historico> eventoHistorico;
    private LocalDateTime createdAt;

}

