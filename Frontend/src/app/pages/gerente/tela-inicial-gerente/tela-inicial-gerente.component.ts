import { Component, Inject, inject, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, DOCUMENT } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { Conta } from '../../../shared/models/conta.model';
import { ModalRejeitarClienteComponent } from '../../../shared/components/modal-rejeitar-cliente/modal-rejeitar-cliente.component';
import { Gerente } from '../../../shared/models/gerente.model';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { Cliente } from '../../../shared/models/cliente.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-tela-inicial-gerente',
  templateUrl: './tela-inicial-gerente.component.html',
  styleUrls: ['./tela-inicial-gerente.component.css'],
  standalone: true,
  imports: [CommonModule, SidebarComponent, ModalRejeitarClienteComponent]
})
export class TelaInicialGerenteComponent implements OnInit {
  @ViewChild('modalDeNegacao') modalComponent!: ModalRejeitarClienteComponent;
  private readonly toastr = inject(ToastrService);

  contasParaAprovar: Conta[] = [];
  gerenteId: number = 0;
  gerente: Gerente | undefined;

  constructor(
    private readonly managerService: GerenteService,
    private readonly route: ActivatedRoute,
    @Inject(DOCUMENT) private readonly document: Document
  ) {}

  ngOnInit(): void {
    this.gerenteId = Number(this.route.snapshot.paramMap.get('id'));
    this.gerente = this.managerService.listManagerById(this.gerenteId);
    this.contasParaAprovar = this.managerService.listCustomersForApprove(this.gerente as Gerente);
  }

  aprovar(cliente: Cliente) {
    const result = this.managerService.approveCustomer(cliente, this.gerente as Gerente);

    if(result.success){
      this.toastr.success('Cliente Aprovado com Sucesso!', 'Sucesso');
      this.document.defaultView?.location.reload();
    }
    else{
      this.toastr.error('Ocorreu um erro ao aprovar o cliente.', 'Erro');
    }
  }

  chamarModal(cliente: Cliente) {
    if(this.gerente){
      this.modalComponent.abrir(cliente, this.gerente);
    }
  }
  
  onPedidoNegado(dados: any) {
    console.log('O componente pai foi notificado!', dados);
    this.toastr.success('Cliente rejeitado com sucesso!', 'Sucesso');
    this.document.defaultView?.location.reload();
  }
}