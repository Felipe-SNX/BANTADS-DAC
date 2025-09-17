import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';

interface AdminDashboard {
  id: number;
  nome: string;
  email: string;
  totalClientes: number;
  saldoPositivo: number;
  saldoNegativo: number;
}

@Component({
  selector: 'app-tela-inicial-admin',
  standalone: true,
  imports: [
    SidebarComponent,
    CommonModule
  ],
  templateUrl: './tela-inicial-admin.component.html',
  styleUrl: './tela-inicial-admin.component.css'
})

export class TelaInicialAdminComponent implements OnInit {
  AdminDashboard: AdminDashboard[] = [];

  ngOnInit(): void {
    const gerentes  = JSON.parse(localStorage.getItem('gerentes') || '[]');
    const contas = JSON.parse(localStorage.getItem('contas') || '[]');
    
    this.AdminDashboard = gerentes.map((gerente: any) => {
      const clientesDoGerente = gerente.clientes || [];
      const totalClientes = clientesDoGerente.length;

      const contasDoGerente = contas
      .filter((conta:any) => clientesDoGerente.some((cliente: any) => cliente.id === conta.cliente.id));
    
      const saldoPositivo = contasDoGerente
      .filter((conta: any) => conta.saldo >= 0)
      .reduce((acc: number, conta: any) => acc + conta.saldo, 0);

      const saldoNegativo = contasDoGerente
      .filter((conta: any) => conta.saldo < 0)
      .reduce((acc: number, conta: any) => acc + conta.saldo, 0); 

      return {
        id: gerente.id,
        nome: gerente.nome,
        email: gerente.email,
        totalClientes: totalClientes,
        saldoPositivo: saldoPositivo,
        saldoNegativo: saldoNegativo
      };
    });

    // Ordena os gerentes pelo maior saldo positivo
    this.AdminDashboard.sort((a, b) => b.saldoPositivo - a.saldoPositivo);

  }
}