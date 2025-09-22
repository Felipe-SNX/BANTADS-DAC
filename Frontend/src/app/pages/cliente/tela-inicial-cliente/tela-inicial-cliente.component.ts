import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ContaService } from '../../../services/conta/conta.service';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { MockDataService } from '../../../services/mock/mock-data.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { Cliente } from '../../../shared/models/cliente.model';
import { Conta } from '../../../shared/models/conta.model';
import { Gerente } from '../../../shared/models/gerente.model';

@Component({
  selector: 'app-tela-inicial-cliente',
  templateUrl: './tela-inicial-cliente.component.html',
  styleUrls: ['./tela-inicial-cliente.component.css'],
  imports: [SidebarComponent],
  standalone: true
})
export class TelaInicialClienteComponent implements OnInit {
  cliente: Cliente = new Cliente();
  conta: Conta = new Conta();  
  saldoNegativo: boolean = false;
  gerente: Gerente = new Gerente();


  constructor
  (
    private readonly mockDataService: MockDataService,
    private readonly clienteService: ClienteService,
    private readonly userService: UserService,
    private readonly accountService: ContaService  ) {

   }

  ngOnInit(): void {        
    const user = this.userService.findLoggedUser();    
    if(user){
      this.loadClienteData(user.idPerfil);
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
        this.gerente = this.conta.gerente;
      }          
    }                        
}
}