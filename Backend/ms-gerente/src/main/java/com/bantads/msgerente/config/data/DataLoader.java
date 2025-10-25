package com.bantads.msgerente.config.data;

import com.bantads.msgerente.core.enums.TipoGerente;
import com.bantads.msgerente.core.model.Gerente;
import com.bantads.msgerente.core.repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final GerenteRepository gerenteRepository;

    @Override
    public void run(String... args) throws Exception {
        if (gerenteRepository.count() == 0) {
            log.info("Nenhum gerente encontrado. Populando o banco de dados com dados de mock...");

            Gerente gerente1 = new Gerente();
            gerente1.setNome("Geniéve");
            gerente1.setEmail("ger1@bantads.com.br");
            gerente1.setCpf("98574307084");
            gerente1.setTelefone("4190909090");
            gerente1.setTipo(TipoGerente.GERENTE);

            Gerente gerente2 = new Gerente();
            gerente2.setNome("Godophredo");
            gerente2.setEmail("ger2@bantads.com.br");
            gerente2.setCpf("64065268052");
            gerente2.setTelefone("4180808080");
            gerente2.setTipo(TipoGerente.GERENTE);

            Gerente gerente3 = new Gerente();
            gerente3.setNome("Gyândula");
            gerente3.setEmail("ger3@bantads.com.br");
            gerente3.setCpf("23862179060");
            gerente3.setTelefone("4170707070");
            gerente3.setTipo(TipoGerente.GERENTE);

            List<Gerente> gerentes = Arrays.asList(gerente1, gerente2, gerente3);

            gerenteRepository.saveAll(gerentes);
            log.info("Foram inseridos {} gerentes no banco de dados.", gerentes.size());
        } else {
            log.info("O banco de dados de gerentes já está populado. Nenhuma ação necessária.");
        }
    }
}
