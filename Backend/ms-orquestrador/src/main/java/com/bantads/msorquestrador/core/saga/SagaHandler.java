package com.bantads.msorquestrador.core.saga;

import com.bantads.msorquestrador.core.enums.EEventSource;
import com.bantads.msorquestrador.core.enums.ESaga;
import com.bantads.msorquestrador.core.enums.ESagaStatus;
import com.bantads.msorquestrador.core.enums.ETopics;

import java.util.Map;

public final class SagaHandler {

    private SagaHandler(){
    }

    public static final Object[][] SAGA_AUTOCADASTRO_HANDLER = {
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.SAGA_STARTED, ETopics.AUTENTICACAO_SUCCESS },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CLIENTE_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },

        { EEventSource.AUTENTICACAO_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.AUTENTICACAO_FAIL },
        { EEventSource.AUTENTICACAO_SERVICE, ESagaStatus.FAIL, ETopics.CLIENTE_FAIL },
        { EEventSource.AUTENTICACAO_SERVICE, ESagaStatus.SUCCESS, ETopics.CONTA_SUCCESS },

        { EEventSource.CONTA_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CONTA_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.AUTENTICACAO_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.GERENTE_SUCCESS },

        { EEventSource.GERENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.GERENTE_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.FAIL, ETopics.CONTA_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS }
    };

    public static final Object[][] SAGA_ALTERACAO_PERFIL_HANDLER = {
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.SAGA_STARTED, ETopics.CONTA_SUCCESS },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CLIENTE_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },

        { EEventSource.CONTA_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CONTA_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.CLIENTE_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS }
    };

    public static final Object[][] SAGA_REMOCAO_GERENTE_HANDLER = {
        { EEventSource.GERENTE_SERVICE, ESagaStatus.SAGA_STARTED, ETopics.CONTA_SUCCESS },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.GERENTE_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },

        { EEventSource.CONTA_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CONTA_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.GERENTE_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.GERENTE_SUCCESS },

        { EEventSource.GERENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.GERENTE_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.FAIL, ETopics.CONTA_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS }
    };

    public static final Object[][] SAGA_INSERCAO_GERENTE_HANDLER = {
        { EEventSource.GERENTE_SERVICE, ESagaStatus.SAGA_STARTED, ETopics.CONTA_SUCCESS },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.GERENTE_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },

        { EEventSource.CONTA_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.CONTA_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.GERENTE_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS }
    };

    private static final Map<ESaga, Object[][]> SAGA_HANDLERS = Map.of(
        ESaga.AUTOCADASTRO_SAGA, SAGA_AUTOCADASTRO_HANDLER,
        ESaga.ALTERACAO_PERFIL_SAGA, SAGA_ALTERACAO_PERFIL_HANDLER,
        ESaga.REMOCAO_GERENTE_SAGA, SAGA_REMOCAO_GERENTE_HANDLER,
        ESaga.INSERCAO_GERENTE_SAGA, SAGA_INSERCAO_GERENTE_HANDLER
    );

    public static Object[][] getHandler(ESaga saga) {
        return SAGA_HANDLERS.get(saga);
    }

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int SAGA_STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;
}
