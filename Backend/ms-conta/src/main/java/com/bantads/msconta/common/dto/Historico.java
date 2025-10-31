package com.bantads.msconta.common.dto;

import java.time.LocalDateTime;

import com.bantads.msconta.common.enums.EEventSource;
import com.bantads.msconta.common.enums.ESagaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
