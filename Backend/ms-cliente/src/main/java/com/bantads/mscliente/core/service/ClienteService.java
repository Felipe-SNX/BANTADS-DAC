package com.bantads.mscliente.core.service;

import com.bantads.mscliente.core.dto.*;
import com.bantads.mscliente.core.dto.mapper.ClienteMapper;
import com.bantads.mscliente.core.exception.ClienteNaoEncontradoException;
import com.bantads.mscliente.core.exception.CpfJaCadastradoException;
import com.bantads.mscliente.core.exception.EnderecoNaoEncontradoException;
import com.bantads.mscliente.core.model.Cliente;
import com.bantads.mscliente.core.model.Endereco;
import com.bantads.mscliente.core.repository.ClienteRepository;
import com.bantads.mscliente.core.repository.EnderecoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; 

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final EmailService emailService;

    public Optional<Cliente> checkCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    public List<ClienteParaAprovarResponse> listarClientes(String filtro) {
        List<Cliente> clientes; 

        if (filtro == null) {
            clientes = clienteRepository.findAllByAprovadoOrderByNomeAsc(true);
        } else if (filtro.equals("para_aprovar")) {
            clientes = listarClientesParaAprovar();
        } else if (filtro.equals("adm_relatorio_clientes")) {
            clientes = clienteRepository.findAllByAprovadoOrderByNomeAsc(true);
        } else {
            clientes = clienteRepository.findAllByAprovadoOrderByNomeAsc(true);
        }

        return clientes.stream()
                .map(this::enriquecerClienteResponseComEndereco)
                .collect(Collectors.toList());
    }

    private List<Cliente> listarClientesParaAprovar() {
        return clienteRepository.findAllByAprovado(false);
    }

    @Transactional
    public ClienteParaAprovarResponse cadastrarCliente(AutoCadastroInfo autoCadastroInfo) {
        Optional<Cliente> clienteExistente = clienteRepository.findByCpf(autoCadastroInfo.getCpf());

        if (clienteExistente.isPresent()) {
            throw new CpfJaCadastradoException("Cliente", autoCadastroInfo.getCpf());
        }

        Cliente cliente = ClienteMapper.autoCadastroInfoToCliente(autoCadastroInfo);

        String[] enderecoParts = parseEndereco(autoCadastroInfo.getEndereco());

        var endereco = Endereco.builder()
                .cep(autoCadastroInfo.getCep())
                .cidade(autoCadastroInfo.getCidade())
                .estado(autoCadastroInfo.getEstado())
                .logradouro(enderecoParts[0]) 
                .numero(enderecoParts[1]) 
                .build();

        Endereco enderecoSalvo = enderecoRepository.save(endereco);
        cliente.setIdEndereco(enderecoSalvo.getId());
        clienteRepository.save(cliente);

        return enriquecerClienteResponseComEndereco(cliente, enderecoSalvo);
    }

    public RelatorioClientesResponse getClientePorCpf(String cpf) {
        Cliente cliente = buscarClientePorCpfEStatus(cpf, true); 
        Endereco endereco = buscarEnderecoPorId(cliente.getIdEndereco(), cpf);

        RelatorioClientesResponse relatorioClientesResponse = ClienteMapper.clienteToRelatorioClientesResponse(cliente);
        relatorioClientesResponse.setCidade(endereco.getCidade());
        relatorioClientesResponse.setEstado(endereco.getEstado());
        relatorioClientesResponse.setEndereco(endereco.getLogradouro().concat(", ").concat(endereco.getNumero()));

        return relatorioClientesResponse;
    }

    @Transactional 
    public PerfilInfo atualizaCliente(PerfilInfo perfilInfo, String cpf) {
        Cliente cliente = buscarClientePorCpf(cpf);
        PerfilInfo dadosAntigos = ClienteMapper.toPerfilInfo(cliente);
        Endereco endereco = buscarEnderecoPorId(cliente.getIdEndereco(), cpf);

        String[] enderecoParts = parseEndereco(perfilInfo.getEndereco());

        endereco.setEstado(perfilInfo.getEstado());
        endereco.setCep(perfilInfo.getCep());
        endereco.setCidade(perfilInfo.getCidade());
        endereco.setLogradouro(enderecoParts[0]);
        endereco.setNumero(enderecoParts[1]);
        enderecoRepository.save(endereco);

        cliente.setNome(perfilInfo.getNome());
        cliente.setEmail(perfilInfo.getEmail());
        cliente.setSalario(perfilInfo.getSalario());
        clienteRepository.save(cliente);

        return dadosAntigos;
    }

    @Transactional
    public DadosClienteConta aprovarCliente(String cpf) {
        Cliente cliente = buscarClientePorCpfEStatus(cpf, false);
        cliente.setAprovado(true);
        cliente.setDataAprovacaoRejeicao(LocalDateTime.now());
        clienteRepository.save(cliente);
        
        DadosClienteConta dadosClienteConta = new DadosClienteConta();
        dadosClienteConta.setEmail(cliente.getEmail());
        dadosClienteConta.setCliente(cliente.getCpf());
        dadosClienteConta.setSalario(cliente.getSalario());
        dadosClienteConta.setGerente(cliente.getGerente());
        return dadosClienteConta;
    }

    @Transactional 
    public void rejeitarCliente(ClienteRejeitadoDto clienteRejeitadoDto, String cpf) {
        Cliente cliente = buscarClientePorCpfEStatus(cpf, false);

        cliente.setAprovado(false);
        cliente.setDataAprovacaoRejeicao(LocalDateTime.now());
        cliente.setMotivoRejeicao(clienteRejeitadoDto.getMotivo());
        cliente.setGerente(clienteRejeitadoDto.getUsuario().getCpf());
        clienteRepository.save(cliente);

        String destinatario = cliente.getEmail();
        String assunto = "Rejeição Cadastro Internet Banking Bantads";
        String corpo = "Olá! \n\n" + "Seu cadastro no banco Bantads foi rejeitado.\n" + "Com a motivação " + clienteRejeitadoDto.getMotivo() +
                "\nEsperamos que possamos conversar novamente no futuro";
        emailService.enviarEmailRejeitado(destinatario, assunto, corpo);
    }

    @Transactional 
    public void deletarCliente(String cpf) {
        Cliente cliente = buscarClientePorCpf(cpf);
        Long idEndereco = cliente.getIdEndereco();

        clienteRepository.delete(cliente);
        enderecoRepository.deleteById(idEndereco);
        
        log.info("Compensação: Cliente {} e Endereço {} deletados.", cpf, idEndereco);
    }

    @Transactional 
    public void atribuirGerente(String cpfCliente, String cpfGerente) {
        Cliente cliente = buscarClientePorCpfEStatus(cpfCliente, false);
        cliente.setGerente(cpfGerente);
        clienteRepository.save(cliente); 
    }

    private Cliente buscarClientePorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente", cpf));
    }
    
    private Cliente buscarClientePorCpfEStatus(String cpf, boolean aprovado) {
        return clienteRepository.findByCpfAndAprovado(cpf, aprovado)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente", cpf));
    }

    private Endereco buscarEnderecoPorId(long idEndereco, String cpfCliente) {
        return enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereco", cpfCliente));
    }

    private String[] parseEndereco(String enderecoCompleto) {
        if (enderecoCompleto == null || enderecoCompleto.isEmpty()) {
            return new String[]{"", ""};
        }
        int commaIndex = enderecoCompleto.lastIndexOf(',');
        
        if (commaIndex == -1) {
            return new String[]{enderecoCompleto.trim(), ""};
        }
        
        String logradouro = enderecoCompleto.substring(0, commaIndex).trim();
        String numero = enderecoCompleto.substring(commaIndex + 1).trim();
        return new String[]{logradouro, numero};
    }

    private ClienteParaAprovarResponse enriquecerClienteResponseComEndereco(Cliente cliente) {
        Endereco endereco = buscarEnderecoPorId(cliente.getIdEndereco(), cliente.getCpf());
        return enriquecerClienteResponseComEndereco(cliente, endereco);
    }

    private ClienteParaAprovarResponse enriquecerClienteResponseComEndereco(Cliente cliente, Endereco endereco) {
        ClienteParaAprovarResponse response = ClienteMapper.toClienteParaAprovarResponse(cliente);
        response.setCidade(endereco.getCidade());
        response.setEstado(endereco.getEstado());
        response.setEndereco(endereco.getLogradouro().concat(", ").concat(endereco.getNumero()));
        return response;
    }
}