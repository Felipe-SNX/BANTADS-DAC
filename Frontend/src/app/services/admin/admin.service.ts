import { Injectable } from '@angular/core';

interface AdminDashboard {
  id: number;
  nome: string;
  email: string;
  totalClientes: number;
  saldoPositivo: number;
  saldoNegativo: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor() { }

  getAdminDashboardData(): AdminDashboard[] {

    const gerentes = JSON.parse(localStorage.getItem('gerentes') || '[]');
    const contas = JSON.parse(localStorage.getItem('contas') || '[]');

    
    const AdminDashboard = gerentes.map((gerente: any) => {

      const clientesDoGerente = gerente.clientes || [];
      const totalClientes = clientesDoGerente.length;

      const contasDoGerente = contas
        .filter((conta: any) => clientesDoGerente.some((cliente: any) => cliente.id === conta.cliente.id));

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

    AdminDashboard.sort((a: any, b: any) => b.saldoPositivo - a.saldoPositivo);

    return AdminDashboard;
  }
}
