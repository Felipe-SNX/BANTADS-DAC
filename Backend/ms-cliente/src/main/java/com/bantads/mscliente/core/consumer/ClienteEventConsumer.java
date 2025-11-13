package com.bantads.mscliente.core.consumer;

import com.bantads.mscliente.core.dto.*;
import com.bantads.mscliente.core.exception.ErroExecucaoSaga; // Supondo que você tenha esta classe
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.bantads.mscliente.common.dto.Evento;
import com.bantads.mscliente.common.enums.EEventSource;
import com.bantads.mscliente.common.enums.ESaga;
import com.bantads.mscliente.common.enums.ESagaStatus;
import com.bantads.mscliente.common.enums.ETopics;
import com.bantads.mscliente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.mscliente.core.producer.ClienteEventProducer;
import com.bantads.mscliente.core.service.ClienteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ClienteEventConsumer {

    private final ClienteService clienteService;
    private final ObjectMapper objectMapper;
    private final ClienteEventProducer clienteEventProducer; 

    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_CLIENTES)
    public void handleAlteracoes(Evento evento, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        log.info("Evento recebido: {}", evento);
        log.info(routingKey);

        ESaga sagaType = evento.getSaga();

        if (routingKey.equals(ETopics.CMD_CLIENTE_COMPENSATE.getTopic())) {
            log.info("Compensando transação para a saga: {}", sagaType);
            compensarTransacao(sagaType, evento);
        } else {
            log.info("Processando transação para a saga: {}", sagaType);
            prosseguirTransacao(sagaType, evento, routingKey);
        }

        log.info("Banco de dados de cliente sincronizado com sucesso");
    }

    private void prosseguirTransacao(ESaga sagaType, Evento evento, String routingKey) {
        try {
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch (sagaType) {
                case AUTOCADASTRO_SAGA:
                    if (routingKey.equals(ETopics.CMD_CLIENTE_CREATE.getTopic())) {
                        AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(
                                rootNode.path("autoCadastroInfo"), AutoCadastroInfo.class
                        );
                        clienteService.cadastrarCliente(autoCadastroInfo);
                        evento.setStatus(ESagaStatus.SUCCESS);
                        
                    } else {
                        AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(
                                rootNode.path("autoCadastroInfo"), AutoCadastroInfo.class
                        );
                        GerenteNumeroContasDto gerenteInfo = objectMapper.treeToValue(
                                rootNode.path("gerenteEscolhido"), GerenteNumeroContasDto.class
                        );
                        clienteService.atribuirGerente(autoCadastroInfo.getCpf(), gerenteInfo.getCpfGerente());
                        evento.setStatus(ESagaStatus.FINISHED);
                    }
                    publicarSucesso(evento); 
                    break;
                case ALTERACAO_PERFIL_SAGA:
                    PerfilInfo perfilInfo = objectMapper.treeToValue(rootNode.path("perfilInfo"), PerfilInfo.class);
                    String cpf = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    PerfilInfo dadosAntigos = clienteService.atualizaCliente(perfilInfo, cpf);
                    adicionarAoNode(rootNode, "dadosAntigos", dadosAntigos); 
                    atualizarPayload(evento, rootNode, sagaType);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento); 
                    break;

                case APROVAR_CLIENTE_SAGA:
                    ClienteParaAprovarRequest usuario = objectMapper.treeToValue(
                            rootNode.path("clienteParaAprovarRequest"), ClienteParaAprovarRequest.class
                    );
                    DadosClienteConta dadosClienteConta = clienteService.aprovarCliente(usuario.getCpf());

                    adicionarAoNode(rootNode, "dadosClienteConta", dadosClienteConta); 
                    atualizarPayload(evento, rootNode, sagaType);

                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento); 
                    break;
                
                case INSERCAO_GERENTE_SAGA:
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(
                            rootNode.path("dadoGerenteInsercao"), DadoGerenteInsercao.class
                    );
                    ContaEscolhidaDto contaEscolhida = objectMapper.treeToValue(
                            rootNode.path("contaEscolhida"), ContaEscolhidaDto.class
                    );
                    clienteService.atribuirGerente(contaEscolhida.getCliente(), dadoGerenteInsercao.getCpf());
                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento); 
                    break;
                case REMOCAO_GERENTE_SAGA:
                    TypeReference<List<ClientesAfetadosRemocaoGerenteDto>> typeRef =
                            new TypeReference<List<ClientesAfetadosRemocaoGerenteDto>>() {};
                    List<ClientesAfetadosRemocaoGerenteDto> clientesAfetados =
                            objectMapper.convertValue(rootNode.path("clientesAfetados"), typeRef);
                    for(ClientesAfetadosRemocaoGerenteDto cliente : clientesAfetados) {
                        clienteService.atribuirGerente(cliente.getCliente(), cliente.getGerenteNovo());
                    }
                    evento.setStatus(ESagaStatus.SUCCESS);
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
                case AUTOCADASTRO_SAGA:
                    AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(
                                rootNode.path("autoCadastroInfo"), AutoCadastroInfo.class
                    );
                    clienteService.deletarCliente(autoCadastroInfo.getCpf());
                    publicarCompensacaoSucesso(evento);
                    break;
                case ALTERACAO_PERFIL_SAGA:
                    String cpf = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    PerfilInfo dadosAntigos = objectMapper.treeToValue(
                            rootNode.path("dadosAntigos"), PerfilInfo.class
                    );
                    clienteService.atualizaCliente(dadosAntigos, cpf);
                    publicarCompensacaoSucesso(evento);
                    break;
                case INSERCAO_GERENTE_SAGA:
                    ContaEscolhidaDto contaEscolhida = objectMapper.treeToValue(
                            rootNode.path("contaEscolhida"), ContaEscolhidaDto.class
                    );
                    clienteService.atribuirGerente(contaEscolhida.getCliente(), contaEscolhida.getGerente());
                    publicarCompensacaoSucesso(evento);
                    break;
                case REMOCAO_GERENTE_SAGA:
                    TypeReference<List<ClientesAfetadosRemocaoGerenteDto>> typeRef =
                            new TypeReference<List<ClientesAfetadosRemocaoGerenteDto>>() {};
                    List<ClientesAfetadosRemocaoGerenteDto> clientesAfetados =
                            objectMapper.convertValue(rootNode.path("clientesAfetados"), typeRef);
                    for(ClientesAfetadosRemocaoGerenteDto cliente : clientesAfetados) {
                        clienteService.atribuirGerente(cliente.getCliente(), cliente.getGerenteAntigo());
                    }
                    publicarCompensacaoSucesso(evento);
                    break;
                default:
                    log.warn("Saga não reconhecida ou não compensável em 'compensarTransacao': {}", sagaType);
                    break;
            }
        } catch (Exception e) {
            log.error("Erro ao compensar a saga {}: {}", sagaType, e.getMessage(), e);
            publicarCompensacaoFalha(evento); 
        }
    }

    private void publicarSucesso(Evento evento) {
        evento.setSource(EEventSource.CLIENTE_SERVICE);
        clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_SUCCESS, evento);
    }

    private void publicarFalha(Evento evento) {
        evento.setSource(EEventSource.CLIENTE_SERVICE);
        evento.setStatus(ESagaStatus.FAIL);
        clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_FAIL, evento);
    }

    private void publicarCompensacaoSucesso(Evento evento) {
        evento.setSource(EEventSource.CLIENTE_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE);
        clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_SUCCESS, evento);
    }

    private void publicarCompensacaoFalha(Evento evento) {
        evento.setSource(EEventSource.CLIENTE_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
        clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_FAIL, evento);
    }

    private <T> void adicionarAoNode(JsonNode rootNode, String key, T value) {
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