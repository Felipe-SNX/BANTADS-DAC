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
        { EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, ETopics.CMD_CLIENTE_CREATE },

        { EEventSource.CLIENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.COMPENSATE_FAILED, ETopics.FINISH_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.COMPENSATE, ETopics.FINISH_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.CMD_AUTENTICACAO_CREATE },

        { EEventSource.AUTH_SERVICE, ESagaStatus.FAIL, ETopics.CMD_CLIENTE_COMPENSATE },
        { EEventSource.AUTH_SERVICE, ESagaStatus.COMPENSATE_FAILED, ETopics.FINISH_FAIL },
        { EEventSource.AUTH_SERVICE, ESagaStatus.COMPENSATE, ETopics.CMD_CLIENTE_COMPENSATE },
        { EEventSource.AUTH_SERVICE, ESagaStatus.SUCCESS, ETopics.CMD_CONTA_CREATE },

        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.CMD_AUTENTICACAO_COMPENSATE },
        { EEventSource.CONTA_SERVICE, ESagaStatus.COMPENSATE_FAILED, ETopics.FINISH_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.COMPENSATE, ETopics.CMD_AUTENTICACAO_COMPENSATE },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS },
    };

    public static final Object[][] SAGA_ALTERACAO_PERFIL_HANDLER = {
        { EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, ETopics.CMD_CLIENTE_CREATE },

        { EEventSource.CLIENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.COMPENSATE, ETopics.FINISH_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.COMPENSATE_FAILED, ETopics.FINISH_FAIL },
        { EEventSource.CLIENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.CMD_CONTA_CREATE },

        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.CMD_CLIENTE_COMPENSATE },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS }
    };

    public static final Object[][] SAGA_REMOCAO_GERENTE_HANDLER = {
        { EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, ETopics.CMD_CONTA_CREATE },

        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.COMPENSATE, ETopics.FINISH_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.COMPENSATE_FAILED, ETopics.FINISH_FAIL },
        { EEventSource.CONTA_SERVICE, ESagaStatus.SUCCESS, ETopics.CMD_GERENTE_CREATE },

        { EEventSource.GERENTE_SERVICE, ESagaStatus.FAIL, ETopics.CMD_CONTA_COMPENSATE },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS }
    };

    public static final Object[][] SAGA_INSERCAO_GERENTE_HANDLER = {
        { EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, ETopics.CMD_GERENTE_CREATE },

        { EEventSource.GERENTE_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.COMPENSATE, ETopics.FINISH_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.COMPENSATE_FAILED, ETopics.FINISH_FAIL },
        { EEventSource.GERENTE_SERVICE, ESagaStatus.SUCCESS, ETopics.CMD_CONTA_CREATE },

        { EEventSource.CONTA_SERVICE, ESagaStatus.FAIL, ETopics.CMD_GERENTE_COMPENSATE },
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
