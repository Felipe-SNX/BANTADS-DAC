import { Component, Output, EventEmitter } from '@angular/core';
import { TipoMovimentacao } from '../../enums/tipoMovimentacao';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
  standalone: true,
  imports: [
    CommonModule
  ]
})
export class SidebarComponent {
  @Output() actionSelected = new EventEmitter<string>();
  
  menuItems = [
    { label: 'Depósito', icon: 'account_balance', action: TipoMovimentacao.DEPOSITO },
    { label: 'Saque', icon: 'payments', action: TipoMovimentacao.SAQUE },
    { label: 'Transferência', icon: 'swap_horiz', action: TipoMovimentacao.TRANSFERENCIA },
    { label: 'Alterar Perfil', icon: 'person', action: 'profile' }
  ];

  onMenuItemClick(action: string) {
    this.actionSelected.emit(action);
  }
}