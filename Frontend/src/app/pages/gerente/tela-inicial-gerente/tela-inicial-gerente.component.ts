import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { ModalRejeitarClienteComponent } from '../../../shared/components/modal-rejeitar-cliente/modal-rejeitar-cliente.component';
import { ToastrService } from 'ngx-toastr';
import { NgxMaskPipe } from 'ngx-mask';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ClienteResponse } from '../../../shared/models/cliente-response.model';
import { ClienteAprovar } from '../../../shared/models/cliente-aprovar.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { UserService } from '../../../services/user/user.service'; 

@Component({
  selector: 'app-tela-inicial-gerente',
  templateUrl: './tela-inicial-gerente.component.html',
  styleUrls: ['./tela-inicial-gerente.component.css'],
  standalone: true,
  imports: [CommonModule, SidebarComponent, ModalRejeitarClienteComponent, NgxMaskPipe, LoadingComponent]
})
export class TelaInicialGerenteComponent implements OnInit {
  @ViewChild('modalDeNegacao') modalComponent!: ModalRejeitarClienteComponent;
  private readonly toastr = inject(ToastrService);

  public loading: boolean = false;
  public clientesParaAprovar: ClienteResponse[] = [];

  constructor(
    private readonly customerService: ClienteService,
    private readonly userService: UserService
  ) {}

  async ngOnInit(): Promise<void> {  
    this.loading = true; 
    try {
      const cpf = this.userService.getCpfUsuario(); 
      
      if (!cpf) {
        throw new Error("CPF do gerente não encontrado.");
      }

      const clientesTemp = await this.customerService.clientesParaAprovar();
      this.clientesParaAprovar = clientesTemp.filter((cliente) => cliente.gerente === cpf);
      
    } catch (error: any) {
      this.toastr.error('Erro ao carregar clientes pendentes.', 'Erro');
      console.error(error);
    } finally {
      this.loading = false; 
    }
  }

  async aprovar(cliente: ClienteResponse) {
    try {
      const clienteAprovar: ClienteAprovar = new ClienteAprovar(cliente.cpf, cliente.nome, cliente.email);
      await this.customerService.aprovarCliente(clienteAprovar, cliente.cpf);
      this.toastr.success('Cliente Aprovado com Sucesso!', 'Sucesso');
      this.removerClienteDaLista(cliente.cpf);
      
    } catch(error: any) {
      this.toastr.error('Ocorreu um erro ao aprovar o cliente.', 'Erro');
    }
  }

  chamarModal(cliente: ClienteResponse) {
    this.modalComponent.abrir(cliente);
  }
  
  onPedidoNegado(clienteRejeitado: ClienteResponse) { 
    console.log('O componente pai foi notificado!', clienteRejeitado);
    this.toastr.success('Cliente rejeitado com sucesso!', 'Sucesso');
    this.removerClienteDaLista(clienteRejeitado.cpf);
  }

  private removerClienteDaLista(cpf: string): void {
    this.clientesParaAprovar = this.clientesParaAprovar.filter(c => c.cpf !== cpf);
  }
}