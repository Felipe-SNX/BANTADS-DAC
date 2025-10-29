package com.bantads.msorquestrador.core.saga;

import com.bantads.msorquestrador.core.enums.EEventSource;
import com.bantads.msorquestrador.core.enums.ESagaStatus;
import com.bantads.msorquestrador.core.enums.ETopics;

public final class SagaAutocadastroHandler {

    private SagaAutocadastroHandler(){
    }

    public static final Object[][] SAGA_AUTOCADASTRO_HANDLER = {

        { EEventSource.CLIENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CLIENTE_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.AUTENTICACAO_SUCCESS },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_AUTOCADASTRO_FAIL },

        { EEventSource.AUTENTICACAO_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.AUTENTICACAO_FAIL },
        { EEventSource.AUTENTICACAO_SERVICE, ESagaStatus.FAIL, ETopics.CLIENTE_FAIL },
        { EEventSource.AUTENTICACAO_SERVICE, ESagaStatus.SUCCESS, ETopics.CONTA_SUCCESS },

        { EEventSource.CONTA_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CONTA_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.AUTENTICACAO_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.GERENTE_SUCCESS },

        { EEventSource.GERENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.GERENTE_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.FAIL, ETopics.CONTA_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_AUTOCADASTRO_SUCCESS }
    };

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int SAGA_STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;
}
