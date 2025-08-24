import { Injectable } from '@angular/core';
import { ClienteService } from '../cliente/cliente.service';
import { Transacao } from '../../shared/models/transacao.model';
import { Conta } from '../../shared/models/conta.model';
import { Cliente } from '../../shared/models/cliente.model';

const LS_CHAVE_MOV = "movimentacoes";
const LS_CHAVE_CONTAS = "contas";

@Injectable({
  providedIn: 'root'
})
export class ContaService {

  constructor(private readonly customerService: ClienteService) { }

  listAccounts(): Conta[]{
    const accounts = localStorage[LS_CHAVE_CONTAS];
    return accounts ? JSON.parse(accounts) : [];
  }

  listTransactions(): Transacao[]{
    const transaction = localStorage[LS_CHAVE_MOV];
    return transaction ? JSON.parse(transaction) : [];
  }

  getAccountByNum(numConta: number): Conta | undefined{
    const accounts = this.listAccounts();
    const account: Conta | undefined = accounts.find((currentAccount) => currentAccount.numConta === numConta)
    return account;
  }

  getAccountByCustomer(customer: Cliente): Conta | undefined{
    const accounts = this.listAccounts();
    const account: Conta | undefined = accounts.find((currentAccount) => currentAccount.cliente.id === customer.id);
    return account;
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

}
