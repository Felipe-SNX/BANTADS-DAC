import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, RouterConfigurationFeature } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ContaService } from '../../../services/conta/conta.service';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { MockDataService } from '../../../services/mock/mock-data.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { Cliente } from '../../../shared/models/cliente.model';
import { Conta } from '../../../shared/models/conta.model';
import { Gerente } from '../../../shared/models/gerente.model';
import { Router } from '@angular/router';
import { DadoCliente } from '../../../shared/models/dados-cliente.model';
import { CommonModule } from '@angular/common';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-tela-inicial-cliente',
  templateUrl: './tela-inicial-cliente.component.html',
  styleUrls: ['./tela-inicial-cliente.component.css'],
  imports: [SidebarComponent,
            CommonModule,
            LoadingComponent
            ],
  standalone: true
})
export class TelaInicialClienteComponent implements OnInit {
  cliente: DadoCliente = new DadoCliente();
  cpf: string = '';
  loading: boolean = false;
  saldoNegativo: boolean = false;

  constructor
  (
    private readonly mockDataService: MockDataService,
    private readonly clienteService: ClienteService,
    private readonly userService: UserService,
    private readonly accountService: ContaService,
    private readonly router: Router) {

   }

  async ngOnInit(): Promise<void> {
    this.loading = true;
    const user = this.userService.isLogged();
    if(user){
      this.cpf = this.userService.getCpfUsuario();
      await this.loadClienteData(this.cpf);
    } else {
      this.router.navigate(['/']);
    }
    this.loading = false;
  }

  async loadClienteData(cpf: string): Promise<void> {

    try {
      this.cliente = await this.clienteService.getCliente(cpf);
      this.saldoNegativo = this.cliente.saldo < 0;
    } catch (error) {
      console.error('Erro ao carregar dados do cliente:', error);
    }
  }
}
