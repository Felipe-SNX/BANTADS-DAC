import { AfterViewInit, Component, EventEmitter, Output } from '@angular/core';
import { Cliente } from '../../models/cliente.model';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { Gerente } from '../../models/gerente.model';
import { Modal } from 'bootstrap';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'modal-rejeitar-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-rejeitar-cliente.component.html',
  styleUrl: './modal-rejeitar-cliente.component.css'
})
export class ModalRejeitarClienteComponent implements AfterViewInit{
  @Output() pedidoNegado = new EventEmitter<any>();
  
  clienteRecebido: Cliente = new Cliente();
  gerenteRecebido: Gerente = new Gerente();

  motivo: string = '';
  private modal!: Modal;

  constructor(
    private readonly managerService: GerenteService
  ){}

  ngAfterViewInit(): void {
    const element = document.getElementById('modalNegarPedido');
    if (element) {
      this.modal = new Modal(element);
    }
  }

  abrir(cliente: Cliente, gerente: Gerente){
    this.clienteRecebido = cliente; 
    this.gerenteRecebido = gerente;
    this.modal.show(); 
  }

  negar(){
    this.managerService.rejectCustomer(this.clienteRecebido as Cliente, this.gerenteRecebido as Gerente, this.motivo);
    this.pedidoNegado.emit({ clienteId: this.clienteRecebido?.id, motivo: this.motivo });

    this.modal.hide();
  }
}
