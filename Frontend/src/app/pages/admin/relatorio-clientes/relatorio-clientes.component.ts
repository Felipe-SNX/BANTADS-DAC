import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { ContaService } from '../../../services/conta/conta.service';
import { Conta } from '../../../shared/models/conta.model';


@Component({
  selector: 'app-relatorio-clientes',
  standalone: true,
  imports: [
    CommonModule,
    SidebarComponent,
    
  ],
  templateUrl: './relatorio-clientes.component.html',
  styleUrl: './relatorio-clientes.component.css'
})
export class RelatorioClientesComponent implements OnInit {

  clientes: any[] = [];

  constructor(
    private readonly contaService: ContaService, 
  ) { }

  ngOnInit(): void {
    
    const contas: Conta[] = this.contaService.listAccounts();
    this.clientes = contas.map((conta: Conta) => ({
      nomeCliente: conta.cliente.nome,
      cpfCliente: conta.cliente.cpf,
      emailCliente: conta.cliente.email,
      salario: conta.cliente.salario,
      numeroConta: conta.numConta,
      saldo: conta.saldo,
      limiteCliente: conta.limite,
      cpfGerente: conta.gerente.cpf,
      nomeGerente: conta.gerente.nome,
    }));

    this.clientes.sort((a, b) => a.nomeCliente.localeCompare(b.nomeCliente));
  }
  
}
