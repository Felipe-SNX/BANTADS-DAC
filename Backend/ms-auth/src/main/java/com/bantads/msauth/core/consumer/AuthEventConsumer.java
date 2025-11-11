package com.bantads.msauth.core.consumer;

import com.bantads.msauth.common.dto.Evento;
import com.bantads.msauth.common.enums.EEventSource;
import com.bantads.msauth.common.enums.ESaga;
import com.bantads.msauth.common.enums.ESagaStatus;
import com.bantads.msauth.common.enums.ETopics;
import com.bantads.msauth.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msauth.core.document.Usuario;
import com.bantads.msauth.core.dto.DadoGerenteAtualizacao;
import com.bantads.msauth.core.dto.DadoGerenteInsercao;
import com.bantads.msauth.core.dto.DadosClienteConta;
import com.bantads.msauth.core.exception.ErroExecucaoSaga;
import com.bantads.msauth.core.producer.AuthEventProducer;
import com.bantads.msauth.core.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AuthEventConsumer {

    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final AuthEventProducer authEventProducer;

    @RabbitListener(queues = RabbitMQConstantes.FILA_AUTH)
    public void handleConsumer(Evento evento, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        log.info("Evento recebido: {}", evento);
        log.info("Routing Key: {}", routingKey);

        ESaga sagaType = evento.getSaga();

        if (routingKey.equals(ETopics.CMD_AUTH_CREATE.getTopic())) {
            log.info("Processando transação para a saga: {}", sagaType);
            prosseguirTransacao(sagaType, evento);
        } else {
            log.info("Compensando transação para a saga: {}", sagaType);
            compensarTransacao(sagaType, evento);
        }

        log.info("Banco de dados de gerente sincronizado com sucesso");
    }

    private void prosseguirTransacao(ESaga sagaType, Evento evento) {
        try {
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch (sagaType) {
                case APROVAR_CLIENTE_SAGA:
                    DadosClienteConta dadosClienteConta = objectMapper.treeToValue(
                        rootNode.path("dadosClienteConta"), DadosClienteConta.class
                    );

                    authService.cadastrarUsuarioCliente(dadosClienteConta);
                    authService.enviarEmailAprovado(dadosClienteConta);

                    evento.setStatus(ESagaStatus.FINISHED);
                    publicarSucesso(evento); 
                    break;

                case INSERCAO_GERENTE_SAGA:
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(
                            rootNode.path("dadoGerenteInsercao"), DadoGerenteInsercao.class
                    );

                    authService.cadastrarUsuarioGerente(dadoGerenteInsercao);

                    evento.setStatus(ESagaStatus.FINISHED);
                    publicarSucesso(evento); 
                    break;

                case REMOCAO_GERENTE_SAGA:
                    String cpfs = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    Usuario usuarioExcluido = authService.excluirUsuario(cpfs);
                    
                    adicionarAoPayload(rootNode, "usuarioExcluido", usuarioExcluido);
                    atualizarPayload(evento, rootNode, sagaType);

                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento); 
                    break;
                case ALTERAR_GERENTE_SAGA:
                    DadoGerenteAtualizacao dadoGerenteAtualizacao = objectMapper.treeToValue(
                            rootNode.path("dadoGerenteAtualizacao"), DadoGerenteAtualizacao.class
                    );
                    String cpfParaAtualizar = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    
                    authService.atualizarSenha(dadoGerenteAtualizacao, cpfParaAtualizar);
                    evento.setStatus(ESagaStatus.FINISHED);
                    publicarSucesso(evento);
                    break;
                default:
                    log.warn("Saga não reconhecida em 'prosseguirTransacao': {}", sagaType);
                    break;
            }
        } catch (Exception e) {
            log.error("Erro ao processar a saga {}: {}", sagaType, e.getMessage(), e);
            publicarFalha(evento);
        }
    }

    private void compensarTransacao(ESaga sagaType, Evento evento) {
        try {
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch (sagaType) {
                case REMOCAO_GERENTE_SAGA:
                    Usuario usuario = objectMapper.treeToValue(
                            rootNode.path("usuarioExcluido"), Usuario.class
                    );
                    
                    authService.cadastrarUsuarioExcluido(usuario);
                    publicarCompensacaoSucesso(evento);
                    break;

                default:
                    log.warn("Saga não reconhecida em 'compensarTransacao': {}", sagaType);
                    break;
            }
        } catch (Exception e) {
            log.error("Erro ao compensar a saga {}: {}", sagaType, e.getMessage(), e);
            publicarCompensacaoFalha(evento); 
        }
    }

    private void publicarSucesso(Evento evento) {
        evento.setSource(EEventSource.AUTH_SERVICE);
        authEventProducer.sendEvent(ETopics.EVT_AUTH_SUCCESS, evento);
    }

    private void publicarFalha(Evento evento) {
        evento.setSource(EEventSource.AUTH_SERVICE);
        evento.setStatus(ESagaStatus.FAIL);
        authEventProducer.sendEvent(ETopics.EVT_AUTH_FAIL, evento);
    }

    private void publicarCompensacaoSucesso(Evento evento) {
        evento.setSource(EEventSource.AUTH_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE);
        authEventProducer.sendEvent(ETopics.EVT_AUTH_FAIL, evento);
    }

    private void publicarCompensacaoFalha(Evento evento) {
        evento.setSource(EEventSource.AUTH_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
        authEventProducer.sendEvent(ETopics.EVT_AUTH_FAIL, evento);
    }

    private <T> void adicionarAoPayload(JsonNode rootNode, String key, T value) {
        if (rootNode instanceof ObjectNode) {
            JsonNode node = objectMapper.valueToTree(value);
            ((ObjectNode) rootNode).set(key, node);
        } else {
            throw new ErroExecucaoSaga("Payload da saga (rootNode) não é um objeto JSON.");
        }
    }

    private void atualizarPayload(Evento evento, JsonNode rootNode, ESaga sagaType){
        try {
            evento.setPayload(objectMapper.writeValueAsString(rootNode));
        } catch (JsonProcessingException e) {
            log.error("Erro crítico ao atualizar o payload na saga {}: {}", sagaType, e.getMessage(), e);
            throw new ErroExecucaoSaga("Erro ao atualizar payload");
        }
    }
}