import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../services/auth/user.service';

interface IMenu{
  label: string,
  icon: string,
  action: string,
  type: string
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
  standalone: true,
  imports: [
    CommonModule
  ]
})
export class SidebarComponent implements OnInit{
  @Input() tipoTela: 'cliente' | 'gerente' = 'cliente';

  menuSelecionado: IMenu[] = [];
  constructor(private readonly router: Router, private readonly userService: UserService){}

  ngOnInit(): void {
    this.menuSelecionado = this.menuItems.filter((menu) => menu.type === this.tipoTela);
  }
    
  title = this.tipoTela === 'cliente' ? 'Menu do Cliente' : 'Menu do Gerente';  

  menuItems: IMenu[] = [
    { label: 'Alterar Perfil', icon: 'person', action: 'alterarPerfil', type: 'cliente' },
    { label: 'Depósito', icon: 'account_balance', action: 'Depósito', type: 'cliente'},
    { label: 'Saque', icon: 'payments', action: 'Saque', type: 'cliente' },
    { label: 'Transferência', icon: 'swap_horiz', action: 'Transferência', type: 'cliente' },
    { label: 'Extrato', icon: 'person', action: 'Extrato', type: 'cliente' },
  ];

  onMenuItemClick(action: string) {
    switch(action){
      case 'alterarPerfil':
        this.router.navigate(['cliente/atualizarCadastro']);
        break;
      case 'Depósito':
        this.router.navigate(['cliente/deposito']);
        break;
      case 'Saque':
        this.router.navigate(['cliente/saque']);
        break;
      case 'Transferência':
        this.router.navigate(['cliente/transferencia']);
        break;
      case 'Extrato':
        this.router.navigate(['cliente/consultaExtrato']);
        break;
      default:
        return;
    }
  }

  logout(){
    this.userService.deleteLoggedUser();
    this.router.navigate(['/']);
  }
}