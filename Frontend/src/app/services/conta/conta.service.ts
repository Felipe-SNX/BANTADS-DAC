import { Injectable } from '@angular/core';
import { Conta } from '../../shared/models/conta.model';
import { Cliente } from '../../shared/models/cliente.model';
import { Gerente } from '../../shared/models/gerente.model';
import { LocalStorageResult } from '../../shared/utils/LocalStorageResult';

const LS_CHAVE_CONTAS = "contas";

@Injectable({
  providedIn: 'root'
})
export class ContaService {

  constructor() { }

  listAccounts(): Conta[]{
    const accounts = localStorage[LS_CHAVE_CONTAS];
    return accounts ? JSON.parse(accounts) : [];
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

  createAccount(customer: Cliente, manager: Gerente): LocalStorageResult{
    const newAccount: Conta = new Conta(this.generateAccountNumber(), customer, new Date(), 0.00, this.calculateLimit(customer.salario), manager);

    const accounts = this.listAccounts();
    accounts.push(newAccount);
    localStorage[LS_CHAVE_CONTAS] = JSON.stringify(accounts);

    return {
      success: true,
      message: 'Conta cadastrada com sucesso!'
    };
  }

  updateAccountLimit(customer: Cliente){
    const accounts = this.listAccounts();

    accounts.forEach((conta) => {
      if(conta.cliente.id === customer.id){
        let limite = this.calculateLimit(customer.salario);
        if(conta.saldo + limite < 0) limite = conta.saldo * -1;
        conta.limite = limite;
      }
    });

    localStorage[LS_CHAVE_CONTAS] = JSON.stringify(accounts);
  }

  updateAccountBalance(account: Conta){
    const accounts = this.listAccounts();

    accounts.forEach((conta) => {
      if(conta.numConta === account.numConta){
        conta.saldo = account.saldo;
      }
    });

    localStorage[LS_CHAVE_CONTAS] = JSON.stringify(accounts);
  }

  updateAccountCustomer(customer: Cliente){
    const accounts = this.listAccounts();
    const account = accounts.findIndex((conta) => conta.cliente.id === customer.id);
    
    if(account !== -1){
      accounts[account].cliente = customer;
    }

    const updatedArray: Conta[] = [];

    accounts.forEach((conta) => {
      updatedArray.push(conta);
    });

    localStorage[LS_CHAVE_CONTAS] = JSON.stringify(updatedArray);
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

  listAccountsByManager(managerId: number): Conta[]{
    const accounts = localStorage[LS_CHAVE_CONTAS];
    var accountsJson = accounts ? JSON.parse(accounts) : [];
    return accountsJson.filter((account: Conta) => account.gerente.id === managerId);
  }
}

