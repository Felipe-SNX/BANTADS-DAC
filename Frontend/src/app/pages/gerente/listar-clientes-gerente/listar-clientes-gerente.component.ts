import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NgxMaskPipe } from 'ngx-mask';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ClienteResponse } from '../../../shared/models/cliente-response.model';
import { Subscription } from 'rxjs'; 

@Component({
  selector: 'app-listar-clientes-gerente',
  templateUrl: './listar-clientes-gerente.component.html',
  styleUrls: ['./listar-clientes-gerente.component.css'],
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskPipe],
  standalone: true
})

export class ListarClientesGerenteComponent implements OnInit, OnDestroy {
  listaId: number = 0;

  private clientesBase: ClienteResponse[] = [];
  public clientesFiltrados: ClienteResponse[] = [];

  public filtroNome: string = '';
  public filtroCpf: string = '';

  private routeSubscription: Subscription | undefined;

  constructor(
    private readonly clienteService: ClienteService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly userService: UserService
  ) { }

  ngOnInit() {
    this.routeSubscription = this.route.paramMap.subscribe(async (params) => {
      this.listaId = Number(params.get('id'));
      await this.carregarEFiltrarClientes();
      this.aplicarFiltrosLocais();
    });
  }

  ngOnDestroy(): void {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe();
    }
  }
  
  async carregarEFiltrarClientes() {
    const gerenteCpf = this.userService.getCpfUsuario();

    try {
      const todosClientes = await this.clienteService.buscarClientes();

      if (todosClientes.length === 0) {
        console.error("Backend não retornou clientes.");
        this.clientesBase = [];
        return;
      }

      const clientesDoGerente = todosClientes.filter((cliente) => cliente.gerente === gerenteCpf);

      switch (this.listaId) {
        case 1:
          this.clientesBase = clientesDoGerente;
          break;
        case 3:
          this.clientesBase = await this.clienteService.buscarTop3Clientes();
          break;
        default:
          console.warn("listaId não reconhecido:", this.listaId);
          this.clientesBase = clientesDoGerente;
      }

    } catch (error) {
      console.error("Falha ao carregar clientes", error);
      this.clientesBase = [];
    }
  }

  public aplicarFiltrosLocais(): void {
    this.clientesFiltrados = this.clientesBase.filter(cliente => {
      const nomeMatch = this.filtroNome
        ? cliente.nome.toLowerCase().includes(this.filtroNome.toLowerCase())
        : true;

      const cpfMatch = this.filtroCpf
        ? cliente.cpf.replace(/\D/g, '').includes(this.filtroCpf.replace(/\D/g, ''))
        : true;

      return nomeMatch && cpfMatch;
    });
  }

  public irParaConsulta(clientecpf: string) {
    this.router.navigate(['gerente/consultaCliente', clientecpf]);
  }
}