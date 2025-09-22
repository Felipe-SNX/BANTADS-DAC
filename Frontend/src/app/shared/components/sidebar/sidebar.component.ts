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
  @Input() tipoTela!: 'cliente' | 'gerente' | 'admin';

  menuSelecionado: IMenu[] = [];
  constructor(private readonly router: Router, private readonly userService: UserService){}

  title: string = '';

  ngOnInit(): void {
    this.menuSelecionado = this.menuItems.filter((menu) => menu.type === this.tipoTela);
    this.title = this.tipoTela === 'cliente' ? 'Menu do Cliente'
      : this.tipoTela === 'gerente' ? 'Menu do Gerente'
      : 'Menu do Administrador';
  }

  menuItems: IMenu[] = [
    { label: 'Alterar Perfil', icon: 'person', action: 'alterarPerfil', type: 'cliente' },
    { label: 'Depósito', icon: 'account_balance', action: 'Depósito', type: 'cliente'},
    { label: 'Saque', icon: 'payments', action: 'Saque', type: 'cliente' },
    { label: 'Transferência', icon: 'swap_horiz', action: 'Transferência', type: 'cliente' },
    { label: 'Extrato', icon: 'person', action: 'Extrato', type: 'cliente' },
    { label: 'Dashboard', icon: 'person', action: 'Dashboard', type: 'admin' },
    { label: 'Listar Gerentes', icon: 'person', action: 'listarGerentes', type: 'admin' },
    { label: 'Novo Gerente', icon: 'person', action: 'adicionarGerente', type: 'admin' },
    { label: 'Relatório de Clientes', icon: 'person', action: 'listarClientes', type: 'admin' },
    
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
      case 'adicionarGerente':
        this.router.navigate(['admin/adicionarGerente']);
        break;
      case 'listarGerentes':
        this.router.navigate(['admin/listarGerentes']);
        break;
      case 'Dashboard':
        this.router.navigate(['admin/:id']);
        break;
      default:
        return;
    }
  }

  onHomeClick(){
    if(this.tipoTela === 'cliente'){
      this.router.navigate(['cliente']);
    }
  }

  logout(){
    this.userService.deleteLoggedUser();
    this.router.navigate(['/']);
  }
}