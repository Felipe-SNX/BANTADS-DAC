import { AfterViewInit, Component, EventEmitter, inject, Output } from '@angular/core';
import { Modal } from 'bootstrap';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteResponse } from '../../models/cliente-response.model';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ClienteMotivoRejeicao } from '../../models/cliente-motivo-rejeicao.model';
import { ToastrService } from 'ngx-toastr'; 

@Component({
  selector: 'modal-rejeitar-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-rejeitar-cliente.component.html',
  styleUrl: './modal-rejeitar-cliente.component.css'
})
export class ModalRejeitarClienteComponent implements AfterViewInit{
  @Output() pedidoNegado = new EventEmitter<ClienteResponse>();
  
  public clienteRecebido: ClienteResponse = new ClienteResponse();
  public motivo: ClienteMotivoRejeicao = new ClienteMotivoRejeicao();
  public isRejeitando: boolean = false; 

  private modal!: Modal;
  private readonly toastr = inject(ToastrService); 

  constructor(
    private readonly customerService: ClienteService
  ){}

  ngAfterViewInit(): void {
    const element = document.getElementById('modalNegarPedido');
    if (element) {
      this.modal = new Modal(element);
    }
  }

  abrir(cliente: ClienteResponse){
    this.clienteRecebido = cliente; 
    this.motivo = new ClienteMotivoRejeicao();
    this.isRejeitando = false;
    this.modal.show(); 
  }

  async negar(){
    if (!this.motivo.motivo || this.motivo.motivo.trim() === '') {
      this.toastr.error('O motivo da rejeição é obrigatório.', 'Erro');
      return;
    }

    this.isRejeitando = true;

    try {
      await this.customerService.rejeitarCliente(this.motivo, this.clienteRecebido.cpf);
      this.pedidoNegado.emit(this.clienteRecebido);
      this.toastr.success('Cliente rejeitado com sucesso!', 'Sucesso'); 
      this.modal.hide();

    } catch (error) {
      console.error("Erro ao rejeitar cliente:", error);
      this.toastr.error('Ocorreu um erro ao rejeitar o cliente.', 'Erro');
    } finally {
      this.isRejeitando = false;
    }
  }
}