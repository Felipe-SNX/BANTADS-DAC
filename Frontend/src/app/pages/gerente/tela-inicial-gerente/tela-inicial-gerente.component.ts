import { Component, Inject, inject, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, DOCUMENT } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { ModalRejeitarClienteComponent } from '../../../shared/components/modal-rejeitar-cliente/modal-rejeitar-cliente.component';
import { Gerente } from '../../../shared/models/gerente.model';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { ToastrService } from 'ngx-toastr';
import { NgxMaskPipe } from 'ngx-mask';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ClienteResponse } from '../../../shared/models/cliente-response.model';
import { ClienteAprovar } from '../../../shared/models/cliente-aprovar.model';

@Component({
  selector: 'app-tela-inicial-gerente',
  templateUrl: './tela-inicial-gerente.component.html',
  styleUrls: ['./tela-inicial-gerente.component.css'],
  standalone: true,
  imports: [CommonModule, SidebarComponent, ModalRejeitarClienteComponent, NgxMaskPipe]
})
export class TelaInicialGerenteComponent implements OnInit {
  @ViewChild('modalDeNegacao') modalComponent!: ModalRejeitarClienteComponent;
  private readonly toastr = inject(ToastrService);

  clientesParaAprovar: ClienteResponse[] = [];
  gerenteId: number = 0;
  gerente: Gerente | undefined;

  constructor(
    private readonly customerService: ClienteService,
    @Inject(DOCUMENT) private readonly document: Document
  ) {}

  async ngOnInit(): Promise<void> {    
    const cpf = sessionStorage.getItem("cpf");
    const clientesTemp = await this.customerService.clientesParaAprovar();

    this.clientesParaAprovar = clientesTemp.filter((cliente) => cliente.gerente === cpf);
  }

  async aprovar(cliente: ClienteResponse) {
    try{
      const clienteAprovar: ClienteAprovar = new ClienteAprovar(cliente.cpf, cliente.nome, cliente.email);
      await this.customerService.aprovarCliente(clienteAprovar, cliente.cpf);
      this.toastr.success('Cliente Aprovado com Sucesso!', 'Sucesso');
      this.document.defaultView?.location.reload();
    } catch(error: any){
      this.toastr.error('Ocorreu um erro ao aprovar o cliente.', 'Erro');
    }

  }

  chamarModal(cliente: ClienteResponse) {
    this.modalComponent.abrir(cliente);
  }
  
  onPedidoNegado(dados: any) {
    console.log('O componente pai foi notificado!', dados);
    this.toastr.success('Cliente rejeitado com sucesso!', 'Sucesso');
    this.document.defaultView?.location.reload();
  }
}