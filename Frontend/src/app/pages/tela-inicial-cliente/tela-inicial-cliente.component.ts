import { Component, OnInit } from '@angular/core';
import { MockDataService } from '../../services/mock/mock-data.service';
import { Cliente } from '../../shared/models/cliente.model';
import { Conta } from '../../shared/models/conta.model';
import { Transacao } from '../../shared/models/transacao.model';
import { ActivatedRoute } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';
import { ClienteService } from '../../services/cliente/cliente.service';
import { User } from '../../shared/models/user.model';
import { UserService } from '../../services/auth/user.service';
import { ContaService } from '../../services/conta/conta.service';
import { GerenteService } from '../../services/gerente/gerente.service';
import { Gerente } from '../../shared/models/gerente.model';

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
  transacoesRecentes: Transacao[] = [];
  saldoNegativo: boolean = false;
  gerente: Gerente = new Gerente();


  constructor
  (private mockDataService: MockDataService,
    private route: ActivatedRoute,
    private clienteService: ClienteService,
    private userService: UserService,
    private accountService: ContaService,
    private managerService: GerenteService   
  ) {

   }

  ngOnInit(): void {        
    const cliente = this.userService.findLoggedUser()?.usuario;    
    if(cliente){
      this.loadClienteData(cliente.id as number);
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