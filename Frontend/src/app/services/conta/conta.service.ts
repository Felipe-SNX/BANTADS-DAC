import { Injectable } from '@angular/core';
import { ClienteService } from '../cliente/cliente.service';
import { Transacao } from '../../shared/models/transacao.model';
import { Conta } from '../../shared/models/conta.model';
import { Cliente } from '../../shared/models/cliente.model';
import { Gerente } from '../../shared/models/gerente.model';

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

  getAccountByNum(numConta: string): Conta | undefined{
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

  createAccount(customer: Cliente, manager: Gerente){
    const newAccount: Conta = new Conta(this.generateAccountNumber(), customer, new Date(), 0.00, this.calculateLimit(customer.salario), manager);

    const accounts = this.listAccounts();
    accounts.push(newAccount);
    localStorage[LS_CHAVE_CONTAS] = JSON.stringify(accounts);

    return {
      success: true,
      message: 'Conta cadastrada com sucesso!'
    };
  }

  private generateAccountNumber(): string{
    let accountNumber: number;
    let proceed: boolean = true;

    do{
      accountNumber = Math.floor(Math.random() * (9999 - 1 + 1)) + 1;

      const alreadyExists = this.getAccountByNum(accountNumber.toString());

      if(!alreadyExists){
        proceed = false;
      }

    }while(proceed);

    return accountNumber.toString();
  }

  private calculateLimit(salario: number){
      return salario/2;
  }
}

