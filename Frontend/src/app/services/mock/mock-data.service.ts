import { Injectable } from '@angular/core';
import { Cliente } from '../../shared/models/cliente.model';
import { Endereco } from '../../shared/models/endereco.model';
import { Gerente } from '../../shared/models/gerente.model';
import { Conta } from '../../shared/models/conta.model';
import { Transacao } from '../../shared/models/transacao.model';
import { TipoMovimentacao } from '../../shared/enums/TipoMovimentacao';
import { Autenticacao } from '../../shared/models/autenticacao.model';
import { TipoUsuario } from '../../shared/enums/TipoUsuario';

@Injectable({
  providedIn: 'root'
})
export class MockDataService {

  constructor() { }

  private readonly endereco1 = new Endereco("Rua", "Estrada 1", 1, "Casa", "10000000", "Curitiba", "Paraná");
  private readonly endereco2 = new Endereco("Avenida", "Avenida 1", 2, "Apartamento 2", "20000000", "Florianópolis", "Santa Catarina");
  private readonly endereco3 = new Endereco("Avenida", "Avenida 2", 2, "", "30000000", "Porto Alegre", "Rio Grande do Sul");

  private readonly cliente1 = new Cliente(1, "Catharyna", "cli1@bantads.com.br", "12912861012", this.endereco1, "4199999999", 10000.00);
  private readonly cliente2 = new Cliente(2, "Cleuddônio", "cli2@bantads.com.br", "09506382000", this.endereco2, "4498888888", 20000.00);
  private readonly cliente3 = new Cliente(3, "Catianna", "cli3@bantads.com.br", "85733854057", this.endereco3, "47977777777", 3000.00);
  private readonly cliente4 = new Cliente(4, "Cutardo", "cli4@bantads.com.br", "58872160006", this.endereco2, "4998000000", 500.00);
  private readonly cliente5 = new Cliente(5, "Coândrya", "cli5@bantads.com.br", "76179646090", this.endereco1, "49989896976", 1500.00);

  private readonly auth1 = new Autenticacao("Catharyna", TipoUsuario.CLIENTE, "cli1@bantads.com.br", "tads");
  private readonly auth2 = new Autenticacao("Cleuddônio", TipoUsuario.CLIENTE, "cli2@bantads.com.br", "tads");
  private readonly auth3 = new Autenticacao("Catianna", TipoUsuario.CLIENTE, "cli3@bantads.com.br", "tads");
  private readonly auth4 = new Autenticacao("Cutardo", TipoUsuario.CLIENTE, "cli4@bantads.com.br", "tads");
  private readonly auth5 = new Autenticacao("Coândrya", TipoUsuario.CLIENTE, "cli5@bantads.com.br", "tads");
  private readonly auth6 = new Autenticacao("Geniéve", TipoUsuario.GERENTE, "ger1@bantads.com.br", "tads");
  private readonly auth7 = new Autenticacao("Godophredo", TipoUsuario.GERENTE, "ger2@bantads.com.br", "tads");
  private readonly auth8 = new Autenticacao("Gyândula", TipoUsuario.GERENTE, "ger3@bantads.com.br", "tads");
  private readonly auth9 = new Autenticacao("Adamântio", TipoUsuario.ADMIN, "adm1@bantads.com.br", "tads");

  private readonly gerente1 = new Gerente(1, "Geniéve", "ger1@bantads.com.br", "98574307084", "4190909090", [this.cliente1, this.cliente4]);
  private readonly gerente2 = new Gerente(2, "Godophredo", "ger2@bantads.com.br", "64065268052", "4180808080", [this.cliente2, this.cliente5]);
  private readonly gerente3 = new Gerente(3, "Gyândula", "ger3@bantads.com.br", "23862179060", "4170707070", [this.cliente3]);
  
  private readonly conta1 = new Conta("1291", this.cliente1, new Date("01/01/2000"), 800.00, 5000.00, this.gerente1);
  private readonly conta2 = new Conta("950", this.cliente2, new Date("10/10/1990"), -10000.00, 10000.00, this.gerente2);
  private readonly conta3 = new Conta("8573", this.cliente3, new Date("12/12/2012"), -1000.00, 1500.00, this.gerente3);
  private readonly conta4 = new Conta("5887", this.cliente4, new Date("02/22/2022"), 150000.00, 0.00, this.gerente1);
  private readonly conta5 = new Conta("7617", this.cliente5, new Date("01/01/2025"), 1500.00, 0.00, this.gerente2);

  private readonly historicoMovimentacao1 = new Transacao(new Date("01/01/2020 10:00"), TipoMovimentacao.DEPOSITO, this.cliente1, null, 1000.00);
  private readonly historicoMovimentacao2 = new Transacao(new Date("01/01/2020 11:00"), TipoMovimentacao.DEPOSITO, this.cliente1, null, 900.00);
  private readonly historicoMovimentacao3 = new Transacao(new Date("01/01/2020 12:00"), TipoMovimentacao.SAQUE, this.cliente1, null, 550.00);
  private readonly historicoMovimentacao4 = new Transacao(new Date("01/01/2020 13:00"), TipoMovimentacao.SAQUE, this.cliente1, null, 350.00);
  private readonly historicoMovimentacao5 = new Transacao(new Date("01/10/2020 15:00"), TipoMovimentacao.DEPOSITO, this.cliente1, null, 2000.00);
  private readonly historicoMovimentacao6 = new Transacao(new Date("01/15/2020 08:00"), TipoMovimentacao.SAQUE, this.cliente1, null, 500.00);
  private readonly historicoMovimentacao7 = new Transacao(new Date("01/20/2020 12:00"), TipoMovimentacao.TRANSFERENCIA, this.cliente1, this.cliente2, 1700.00);
  private readonly historicoMovimentacao8 = new Transacao(new Date("01/01/2025 12:00"), TipoMovimentacao.DEPOSITO, this.cliente2, null, 1000.00);
  private readonly historicoMovimentacao9 = new Transacao(new Date("01/02/2025 10:00"), TipoMovimentacao.DEPOSITO, this.cliente2, null, 5000.00);
  private readonly historicoMovimentacao10 = new Transacao(new Date("01/10/2025 10:00"), TipoMovimentacao.SAQUE, this.cliente2, null, 200.00);
  private readonly historicoMovimentacao11 = new Transacao(new Date("02/05/2025 10:00"), TipoMovimentacao.DEPOSITO, this.cliente2, null, 7000.00);
  private readonly historicoMovimentacao12 = new Transacao(new Date("05/05/2025 00:00"), TipoMovimentacao.DEPOSITO, this.cliente3, null, 1000.00);
  private readonly historicoMovimentacao13 = new Transacao(new Date("05/06/2025 00:00"), TipoMovimentacao.SAQUE, this.cliente3, null, 2000.00);
  private readonly historicoMovimentacao14 = new Transacao(new Date("06/01/2025 00:00"), TipoMovimentacao.DEPOSITO, this.cliente4, null, 150000.00);
  private readonly historicoMovimentacao15 = new Transacao(new Date("07/01/2025 00:00"), TipoMovimentacao.DEPOSITO, this.cliente5, null, 1500.00);

  
  public loadMockData(): void {
    const mockCustomers = [
      this.cliente1,
      this.cliente2,
      this.cliente3,
      this.cliente4,
      this.cliente5
    ];

    const mockManagers = [
      this.gerente1,
      this.gerente2,
      this.gerente3
    ];

    const mockContas = [
      this.conta1,
      this.conta2,
      this.conta3,
      this.conta4,
      this.conta5
    ];

    const mockMovimentacoes = [
      this.historicoMovimentacao1,
      this.historicoMovimentacao2,
      this.historicoMovimentacao3,
      this.historicoMovimentacao4,
      this.historicoMovimentacao5,
      this.historicoMovimentacao6,
      this.historicoMovimentacao7,
      this.historicoMovimentacao8,
      this.historicoMovimentacao9,
      this.historicoMovimentacao10,
      this.historicoMovimentacao11,
      this.historicoMovimentacao12,
      this.historicoMovimentacao13,
      this.historicoMovimentacao14,
      this.historicoMovimentacao15
    ];
    
    const mockAuth = [
      this.auth1,
      this.auth2,
      this.auth3,
      this.auth4,
      this.auth5,
      this.auth6,
      this.auth7,
      this.auth8,
      this.auth9
    ];

    // Verifica se os dados já existem para não ficar sobrescrevendo sem necessidade
    if (!localStorage.getItem('clientes')) {
      localStorage.setItem('clientes', JSON.stringify(mockCustomers));
    }

    if (!localStorage.getItem('gerentes')) {
      localStorage.setItem('gerentes', JSON.stringify(mockManagers));
    }

    if (!localStorage.getItem('contas')) {
      localStorage.setItem('contas', JSON.stringify(mockContas));
    }

    if (!localStorage.getItem('movimentacoes')) {
      localStorage.setItem('movimentacoes', JSON.stringify(mockMovimentacoes));
    }

    if (!localStorage.getItem('autenticacao')) {
      localStorage.setItem('autenticacao', JSON.stringify(mockAuth));
    }
  }
}
