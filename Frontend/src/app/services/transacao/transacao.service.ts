import { Injectable } from '@angular/core';
import { Transacao } from '../../shared/models/transacao.model';
import { ClienteService } from '../cliente/cliente.service';
import { ContaService } from '../conta/conta.service';
import { TipoMovimentacao } from '../../shared/enums/TipoMovimentacao';
import { Conta } from '../../shared/models/conta.model';
import { Cliente } from '../../shared/models/cliente.model';

const LS_CHAVE_MOV = "movimentacoes";

export interface SaveResult {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransacaoService {

  constructor(private readonly customerService: ClienteService, private readonly accountService: ContaService) { }

  listTransactions(): Transacao[]{
    const transaction = localStorage[LS_CHAVE_MOV];
    return transaction ? JSON.parse(transaction) : [];
  }

  listCustomerTransactions(id: number): Transacao[]{
    const customer = this.customerService.getClientById(id);
    const transactions = this.listTransactions();
    const customerTransactions = transactions.filter(
      (currentTransaction) => 
        currentTransaction.clienteDestino?.id === customer?.id || currentTransaction.clienteOrigem?.id === customer?.id
    )
  
    return customerTransactions;
  }

  public registerNewTransaction(transacao: Transacao): SaveResult {

    //Valida
    const validationResult = this.validateTransactionInput(transacao);
    if (!validationResult.success) {
      return validationResult;
    }

    //Verifica conta origem
    const contaOrigem = this.accountService.getAccountByCustomer(transacao.clienteOrigem as Cliente);
    if (!contaOrigem) {
      return { success: false, message: 'Conta de origem não encontrada.' };
    }

    //Verifica Conta Destino
    let contaDestino: Conta | null | undefined = null;
    if (transacao.tipo === TipoMovimentacao.TRANSFERENCIA) {
      contaDestino = this.accountService.getAccountByCustomer(transacao.clienteDestino!);
      if (!contaDestino) {
        return { success: false, message: 'Conta de destino não encontrada.' };
      }
      if (contaOrigem.numConta === contaDestino.numConta) {
          return { success: false, message: 'A conta de origem e destino não podem ser a mesma.' };
      }
    }

    //Executa as alterações
    const updateResult = this.executeTransactionUpdate(transacao, contaOrigem, contaDestino);
    if (!updateResult.success) {
      return updateResult; 
    }

    //Salva a transação
    this.saveTransaction(transacao);

    return { success: true, message: 'Transação registrada com sucesso!' };
  }

  private validateTransactionInput(transacao: Transacao): SaveResult {
    if (!transacao.valor || transacao.valor <= 0) {
      return { success: false, message: 'O valor da transação deve ser um número positivo.' };
    }
    if (!transacao.clienteOrigem) {
      return { success: false, message: 'O cliente de origem é obrigatório.' };
    }
    if (transacao.tipo === TipoMovimentacao.TRANSFERENCIA && !transacao.clienteDestino) {
      return { success: false, message: 'O cliente de destino é obrigatório para transferências.' };
    }
    if (transacao.tipo === TipoMovimentacao.SALDO) {
      return { success: false, message: 'Não é possível registrar uma transação do tipo "SALDO".' };
    }
    return { success: true, message: '' };
  }

  private executeTransactionUpdate(transacao: Transacao, contaOrigem: Conta, contaDestino: Conta | null): SaveResult {
    switch (transacao.tipo) {
      case TipoMovimentacao.SAQUE:
        return this.executeSaque(transacao, contaOrigem);
      case TipoMovimentacao.DEPOSITO:
        return this.executeDeposito(transacao, contaOrigem);
      case TipoMovimentacao.TRANSFERENCIA:
        return this.executeTransferencia(transacao, contaOrigem, contaDestino!);
      default:
        return { success: false, message: 'Tipo de movimentação inválido ou não suportado.' };
    }
  }

  private hasSaldoSuficiente(conta: Conta, valor: number): boolean {
    return (conta.saldo + conta.limite) >= valor;
  }

  private executeSaque(transacao: Transacao, conta: Conta): SaveResult {
    if (!this.hasSaldoSuficiente(conta, transacao.valor)) {
      return { success: false, message: 'Saldo insuficiente para realizar o saque.' };
    }
    conta.saldo -= transacao.valor;
    this.accountService.updateAccount(conta);
    return { success: true, message: 'Saque efetuado.' };
  }

  private executeDeposito(transacao: Transacao, conta: Conta): SaveResult {
    conta.saldo += transacao.valor;
    this.accountService.updateAccount(conta);
    return { success: true, message: 'Depósito efetuado.' };
  }

  private executeTransferencia(transacao: Transacao, origem: Conta, destino: Conta): SaveResult {
    if (!this.hasSaldoSuficiente(origem, transacao.valor)) {
      return { success: false, message: 'Saldo insuficiente para realizar a transferência.' };
    }
    origem.saldo -= transacao.valor;
    destino.saldo += transacao.valor;
    this.accountService.updateAccount(origem);
    this.accountService.updateAccount(destino);
    return { success: true, message: 'Transferência efetuada.' };
  }

  private saveTransaction(transacao: Transacao): void {
    const transactions = this.listTransactions(); 
    transactions.push(transacao);
    localStorage.setItem(LS_CHAVE_MOV, JSON.stringify(transactions));
  }

}
