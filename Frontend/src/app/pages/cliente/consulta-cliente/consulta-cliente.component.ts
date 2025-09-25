import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { MockDataService } from '../../../services/mock/mock-data.service';
import { UserService } from '../../../services/user/user.service';
import { ContaService } from '../../../services/conta/conta.service';
import { Cliente } from '../../../shared/models/cliente.model';
import { Conta } from '../../../shared/models/conta.model';
import { ActivatedRoute } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-consulta-cliente',
  templateUrl: './consulta-cliente.component.html',
  styleUrls: ['./consulta-cliente.component.css'],
    imports: [SidebarComponent,CommonModule, FormsModule],
    standalone: true
})
export class ConsultaClienteComponent implements OnInit {
  cliente: Cliente = new Cliente();
  conta: Conta = new Conta();  
  saldoNegativo: boolean = false;
  clienteId: number = 0;
  clienteEncontrado: boolean = false;
  cpf: string = '';
  erroMensagem: string = '';

  constructor(
    private readonly mockDataService: MockDataService,
    private readonly clienteService: ClienteService,
    private readonly userService: UserService,
    private readonly accountService: ContaService,
    private readonly route: ActivatedRoute,
    ) { }

  ngOnInit() {
    this.clienteId = Number(this.route.snapshot.paramMap.get('id'));
    console.log(this.clienteId);
    if(this.clienteId > 0){
      this.loadClienteData(this.clienteId);
      this.clienteEncontrado = true;   
    }
  }

  loadClienteData(clienteId: number): void {    
    const cliente = this.clienteService.getClientById(clienteId);
    if (cliente) {
      this.cliente = cliente;
      this.saldoNegativo = this.conta.saldo < 0;    
      const conta = this.accountService.getAccountByCustomer(this.cliente);
      if (conta) {
        this.conta = conta;        
        this.saldoNegativo = this.conta.saldo < 0;           
      }          
    }  
  }

  consultarCliente(): void {
    const cliente = this.clienteService.getClientByCpf(this.cpf);
    if (cliente) {
      this.cliente = cliente;
      this.saldoNegativo = this.conta.saldo < 0;    
      const conta = this.accountService.getAccountByCustomer(this.cliente);
      if (conta) {
        this.conta = conta;        
        this.saldoNegativo = this.conta.saldo < 0;           
      }
    }
  }
}