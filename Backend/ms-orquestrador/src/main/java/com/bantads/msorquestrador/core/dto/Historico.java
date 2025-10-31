package com.bantads.msorquestrador.core.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import java.time.LocalDateTime;

import com.bantads.msorquestrador.core.enums.EEventSource;
import com.bantads.msorquestrador.core.enums.ESagaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Historico {

    @Enumerated(EnumType.STRING)
    private EEventSource source;
    @Enumerated(EnumType.STRING)
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
