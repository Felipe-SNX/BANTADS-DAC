package com.bantads.msconta.config.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.bantads.msconta.conta.command.service.DataService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner{

    private final DataService dataService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        dataService.popularBanco();
    }

}
