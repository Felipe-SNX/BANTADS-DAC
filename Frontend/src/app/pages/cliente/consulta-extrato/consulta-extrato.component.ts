import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../services/user/user.service';
import { TransacaoService } from '../../../services/transacao/transacao.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { TipoMovimentacao } from '../../../shared/enums/TipoMovimentacao';
import { Conta } from '../../../shared/models/conta.model';
import { Transacao } from '../../../shared/models/transacao.model';
import { User } from '../../../shared/models/user.model';

@Component({
  selector: 'app-consulta-extrato',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './consulta-extrato.component.html',
  styleUrl: './consulta-extrato.component.css'
})
export class ConsultaExtratoComponent implements OnInit{
  public transacoes: Transacao[] = [];
  public dataInicio: string | null = null;
  public dataFim: string | null = null;
  
  user: User | null | undefined;
  conta: Conta | undefined;
  id: number = 0;
  
  constructor(
    private readonly transactionService: TransacaoService,
    private readonly router: Router,
    private readonly userService: UserService,
  ){}

  ngOnInit(): void {
    const temp = this.userService.findLoggedUser();

    if(!temp) this.router.navigate(['/']);

    this.user = temp; 

    if(this.user?.idPerfil){
      this.id = this.user.idPerfil;
      this.transacoes = this.transactionService.listCustomerTransactions(this.id);
      this.transacoes = this.processTransactions(this.transacoes);
    }
    else{
      this.router.navigate(['/']);
    }
  }

  processTransactions(transactions: Transacao[]): Transacao[]{
    if(transactions.length === 0) return [];

    const ordenedTransactions = [...transactions].sort(
      (a, b) => new Date(a.data).getTime() - new Date(b.data).getTime()
    );

    const groupByDate = new Map<string, Transacao[]>();

    ordenedTransactions.forEach(t => {
      const dayKey = new Date(t.data).toISOString().split('T')[0];
      const list = groupByDate.get(dayKey) || [];
      list.push(t);
      groupByDate.set(dayKey, list);
    })

    let finalResult: Transacao[] = [];
    let accumulatorBalance = 0;

    const startDate = new Date(ordenedTransactions[0].data);
    const endDate = new Date(ordenedTransactions[ordenedTransactions.length - 1].data);

    let currentDate = new Date(startDate.toISOString().split('T')[0]);

    while(currentDate <= endDate){

      const dayKey = currentDate.toISOString().split('T')[0];
      const dayTransactions = groupByDate.get(dayKey) || []; 

      if (dayTransactions.length > 0) {
        finalResult.push(...dayTransactions);
      }

      const balanceDay = dayTransactions.reduce((accumulator, transacao) => {
        if (transacao.tipo === TipoMovimentacao.TRANSFERENCIA) {
          if (transacao.clienteDestino?.id === this.id) {
            return accumulator + transacao.valor;
          }
          if (transacao.clienteOrigem?.id === this.id) {
            return accumulator - transacao.valor;
          }
          return accumulator;
        } else if (transacao.tipo === TipoMovimentacao.SAQUE) {
          return accumulator - transacao.valor;
        } else {
          return accumulator + transacao.valor;
        }
      }, 0);

      accumulatorBalance += balanceDay;

      const [year, month, day] = dayKey.split('-').map(Number);
      const transactionsDate = new Date(year, month - 1, day);

      const balanceDate = new Date(transactionsDate);
      balanceDate.setDate(transactionsDate.getDate() + 1);

      //isso é para não aparecer o balanço do fim do dia antes do final
      if(balanceDate.getTime() > endDate.getTime()){
        break;
      }

      finalResult.push({
        tipo: TipoMovimentacao.SALDO,
        clienteOrigem: null,
        clienteDestino: null,
        data: balanceDate, 
        valor: accumulatorBalance
      });

      currentDate.setUTCDate(currentDate.getUTCDate() + 1);
    }

    finalResult = this.sortTransactions(finalResult);
    return finalResult;
  }

  sortTransactions(transaction: Transacao[]): Transacao[]{
    const sortTransactions = transaction.sort((a, b) => {
      const dataA = a.data ? new Date(a.data).getTime() : null;
      const dataB = b.data ? new Date(b.data).getTime() : null;

      if (dataA && !dataB) return -1;
      if (!dataA && dataB) return 1;
      if (!dataA && !dataB) return 0;

      const result = dataB! - dataA!;

      if(result === 0 && b.tipo === TipoMovimentacao.SALDO) return -1

      return 1;
    })

    return sortTransactions;
  }

  filterCustomerTransactionsForDate() {
    const transactions = this.processTransactions(this.transactionService.listCustomerTransactions(this.id));

    if (!this.dataInicio && !this.dataFim) {
      this.transacoes = this.processTransactions(transactions);
      return;
    }

    const dataInicioFiltro = this.dataInicio ? new Date(this.dataInicio + 'T00:00:00') : null;

    let dataFimFiltro = null;
    if (this.dataFim) {
      dataFimFiltro = new Date(this.dataFim + 'T00:00:00');
      dataFimFiltro.setDate(dataFimFiltro.getDate() + 1);
    }

    const filteredTransactions = transactions.filter((transaction) => {
      if (!transaction.data) {
        return false;
      }
      
      const transactionTime = new Date(transaction.data).getTime();

      const atendeFiltroInicio = !dataInicioFiltro || transactionTime >= dataInicioFiltro.getTime();
      const atendeFiltroFim = !dataFimFiltro || transactionTime < dataFimFiltro.getTime();

      return atendeFiltroInicio && atendeFiltroFim;
    });

    this.transacoes = filteredTransactions;
  }
}
