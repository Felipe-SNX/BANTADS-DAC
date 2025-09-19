import { Component, Input, OnInit } from '@angular/core';
import { Cliente } from '../../models/cliente.model';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { Gerente } from '../../models/gerente.model';

@Component({
  selector: 'modal-rejeitar-cliente',
  standalone: true,
  imports: [],
  templateUrl: './modal-rejeitar-cliente.component.html',
  styleUrl: './modal-rejeitar-cliente.component.css'
})
export class ModalRejeitarClienteComponent{
  @Input() clienteRecebido: Cliente | undefined;
  @Input() gerenteRecebido: Gerente | undefined;

  motivo: string = '';
  
  constructor(
    private readonly managerService: GerenteService
  ){}

  negar(){
    this.managerService.rejectCustomer(this.clienteRecebido as Cliente, this.gerenteRecebido as Gerente, this.motivo);
    window.location.reload();
  }
}
