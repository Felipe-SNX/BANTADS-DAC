import { Injectable } from '@angular/core';
import { Cliente } from '../../shared/models/cliente.model';
import { Endereco } from '../../shared/models/endereco.model';
import { Gerente } from '../../shared/models/gerente.model';

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

  private readonly gerente1 = new Gerente(1, "Geniéve", "ger1@bantads.com.br", "98574307084", "4190909090", [this.cliente1, this.cliente4]);
  private readonly gerente2 = new Gerente(2, "Godophredo", "ger2@bantads.com.br", "64065268052", "4180808080", [this.cliente2, this.cliente5]);
  private readonly gerente3 = new Gerente(3, "Gyândula", "ger3@bantads.com.br", "23862179060", "4170707070", [this.cliente3]);
  
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

    // Verifica se os dados já existem para não ficar sobrescrevendo sem necessidade
    if (!localStorage.getItem('clientes')) {
      localStorage.setItem('clientes', JSON.stringify(mockCustomers));
    }

    if (!localStorage.getItem('gerentes')) {
      localStorage.setItem('gerentes', JSON.stringify(mockManagers));
    }
  }
}
