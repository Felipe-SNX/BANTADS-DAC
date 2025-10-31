package com.bantads.msgerente.core.dto;

import com.bantads.msgerente.core.enums.EEventSource;
import com.bantads.msgerente.core.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
