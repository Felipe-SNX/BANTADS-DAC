package com.bantads.msorquestrador.core.dto;

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

    private EEventSource source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
