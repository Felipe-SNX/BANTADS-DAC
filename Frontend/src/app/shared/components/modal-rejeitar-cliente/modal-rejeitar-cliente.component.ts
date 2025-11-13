import { AfterViewInit, Component, EventEmitter, Output } from '@angular/core';
import { Cliente } from '../../models/cliente.model';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { Gerente } from '../../models/gerente.model';
import { Modal } from 'bootstrap';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteResponse } from '../../models/cliente-response.model';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ClienteMotivoRejeicao } from '../../models/cliente-motivo-rejeicao.model';

@Component({
  selector: 'modal-rejeitar-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-rejeitar-cliente.component.html',
  styleUrl: './modal-rejeitar-cliente.component.css'
})
export class ModalRejeitarClienteComponent implements AfterViewInit{
  @Output() pedidoNegado = new EventEmitter<any>();
  
  clienteRecebido: ClienteResponse = new ClienteResponse();

  motivo: ClienteMotivoRejeicao = new ClienteMotivoRejeicao();
  private modal!: Modal;

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
    this.modal.show(); 
  }

  async negar(){
    await this.customerService.rejeitarCliente(this.motivo, this.clienteRecebido.cpf);
    this.pedidoNegado.emit({ clienteCpf: this.clienteRecebido?.cpf, motivo: this.motivo.motivo });

    this.modal.hide();
  }
}
