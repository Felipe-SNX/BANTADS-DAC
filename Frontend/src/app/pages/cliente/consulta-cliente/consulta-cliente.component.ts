import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { MockDataService } from '../../../services/mock/mock-data.service';
import { UserService } from '../../../services/user/user.service';
import { ContaService } from '../../../services/conta/conta.service';
import { Cliente } from '../../../shared/models/cliente.model';
import { Conta } from '../../../shared/models/conta.model';
import { ActivatedRoute } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-consulta-cliente',
  templateUrl: './consulta-cliente.component.html',
  styleUrls: ['./consulta-cliente.component.css'],
    imports: [SidebarComponent],
    standalone: true
})
export class ConsultaClienteComponent implements OnInit {
  cliente: Cliente = new Cliente();
  conta: Conta = new Conta();  
  saldoNegativo: boolean = false;
  clienteId: number = 0;
  clienteEncontrado: boolean = false;

  constructor(
    private readonly mockDataService: MockDataService,
    private readonly clienteService: ClienteService,
    private readonly userService: UserService,
    private readonly accountService: ContaService,
    private readonly route: ActivatedRoute,
    ) { }

  ngOnInit() {
    this.clienteId = Number(this.route.snapshot.paramMap.get('id'));
    if(this.clienteId > 0){
      this.loadClienteData(this.clienteId);
      this.clienteEncontrado = this.cliente.id > 0;    
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

}
