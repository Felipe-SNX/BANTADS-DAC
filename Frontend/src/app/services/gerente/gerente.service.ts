import { Injectable } from '@angular/core';
import { Gerente } from '../../shared/models/gerente.model';
import { Cliente } from '../../shared/models/cliente.model';
import { ContaService } from '../conta/conta.service';
import { LocalStorageResult } from '../../shared/utils/LocalStorageResult';
import { UserService } from '../auth/user.service';
import { User } from '../../shared/models/user.model';
import { TipoUsuario } from '../../shared/enums/TipoUsuario';
import { ClienteService } from '../cliente/cliente.service';

const LS_CHAVE = "gerentes";

@Injectable({
  providedIn: 'root'
})
export class GerenteService {

  constructor(
    private readonly accountService: ContaService,
    private readonly userService: UserService,
    private readonly customerService: ClienteService
  ) { }

  listManagers(): Gerente[] {
    const managers = localStorage[LS_CHAVE];
    return managers ? JSON.parse(managers) : [];
  }
  
  listManagerById(id: number): Gerente | undefined {
    const idNumerico = Number(id); 
    const managers: Gerente[] = this.listManagers();
    const manager: Gerente | undefined = managers.find((currentManager) => currentManager.id === idNumerico)
    return manager;
  }

  listCustomersForApprove(manager: Gerente){
    const accounts = this.accountService.listAccounts();

    const filteredAccounts = accounts.filter((account) => {
      if(account.gerente.id === manager.id && !account.cliente.statusConta.status && account.cliente.statusConta.motivo === null){
        return account;
      }
      else{
        return;
      }
    })

    return filteredAccounts;
  }

  createManager(manager: Gerente): LocalStorageResult {
    const managers = this.listManagers();

    const checkManager = managers.find((currentManager) => currentManager.cpf === manager.cpf);

    if(checkManager){
      return {
        success: false,
        message: `Erro: O CPF ${manager.cpf} já está cadastrado.`
      };
    }

    this.balanceCustomersToNewManager(manager, managers);

    managers.push(manager);
    localStorage[LS_CHAVE] = JSON.stringify(managers);

    return {
      success: true,
      message: 'Gerente cadastrado com sucesso!'
    };
  }

  private balanceCustomersToNewManager(newManager: Gerente, managers: Gerente[]){

    if(managers.length === 0 || (managers.length === 1 && managers[0].clientes.length === 1)){
      return;
    }

    const chosenManager = managers.reduce((managerMoreCustomers, currentManager) => {
      if(managerMoreCustomers.clientes.length < currentManager.clientes.length){
        return currentManager;
      }
      else if(managerMoreCustomers.clientes.length === currentManager.clientes.length){
        return this.calculateManagerBalance(managerMoreCustomers, currentManager);
      }
      else{
        return managerMoreCustomers;
      }
    });

    if(chosenManager.clientes.length === 1) return;
    
    this.moveCustomerToNewManager(newManager, chosenManager);
  }

  private moveCustomerToNewManager(newManager: Gerente, oldManager: Gerente){
    const customer = oldManager.clientes.pop();
    newManager.clientes.push(customer!);
  }

  private calculateManagerBalance(manager1: Gerente, manager2: Gerente): Gerente{
    const manager1Balance = this.calculateManagerTotalBalance(manager1);
    const manager2Balance = this.calculateManagerTotalBalance(manager2);

    if(manager1Balance < manager2Balance){
      return manager1;
    }
    else {
      return manager2;
    }
  }

  private calculateManagerTotalBalance(manager: Gerente){
    let total = 0;

    manager.clientes.forEach((currentCustomer) => {
      const customerBalance = this.accountService.getAccountByCustomer(currentCustomer);
      if(customerBalance) total += customerBalance.saldo;
    });

    return total;
  }

  updateManager(manager: Gerente): LocalStorageResult {
    const managers = this.listManagers();

    const checkManager = managers.findIndex((currentManager) => currentManager.cpf === manager.cpf);

    if(checkManager === -1){
      return {
        success: false,
        message: `Erro: Não foi encontrado nenhum gerente com o CPF ${manager.cpf}`
      };
    }

    manager.id = managers[checkManager].id;
    manager.cpf = managers[checkManager].cpf;
    manager.clientes = managers[checkManager].clientes;

    managers[checkManager] = manager;
    localStorage[LS_CHAVE] = JSON.stringify(managers);

    return {
      success: true,
      message: 'Gerente Atualizado com sucesso!'
    };
  }

  deleteManager(id: number): LocalStorageResult{
    const managers = this.listManagers();
    const findIndex = managers.findIndex((manager) => manager.id === id);

    if(findIndex === -1){
      return {
        success: false,
        message: `Erro: Não foi encontrado nenhum gerente com o id ${id}`
      };
    }

    if(managers.length === 1){
      return {
        success: false,
        message: `Erro: Não é possível excluir o último gerente cadastrado`
      }
    }
    
    const customers = managers[findIndex].clientes;
    managers.splice(findIndex, 1);
    localStorage[LS_CHAVE] = JSON.stringify(managers);

    customers.forEach((customer) => {
      this.addCustomerToManager(customer);
    });

    return {
      success: true,
      message: 'Gerente deletado com sucesso!' 
    }
  }
  
  addCustomerToManager(customer: Cliente): void {
    const managers = this.listManagers();

    const chosenManager = managers.reduce((managerLessCustomers, currentManager) => {
      if (currentManager.clientes.length < managerLessCustomers.clientes.length) {
        return currentManager;
      } else {
        return managerLessCustomers;
      }
    });

    //Necessário criar um novo array para que reflita a alteração no local storage se não o angular não vê necessidade em alterar
    const updatedManagers = managers.map(manager => {
      if (manager.id === chosenManager.id) { 
        return {
          ...manager, 
          clientes: [...manager.clientes, customer] 
        };
      }
      return manager;
    });

    localStorage.setItem(LS_CHAVE, JSON.stringify(updatedManagers));
  }

  approveCustomer(customer: Cliente, manager: Gerente): LocalStorageResult{

    customer.statusConta.status = true;
    customer.statusConta.motivo = null;
    customer.statusConta.dataAvaliacao = new Date();
    customer.statusConta.gerenteAvaliador = manager;
    this.customerService.updateClient(customer);

    const result = this.accountService.createAccount(customer, manager);
    
    if(result.success){
      const user: User = new User(TipoUsuario.CLIENTE, customer.email, customer.cpf, customer.id);
      this.userService.createUserAccount(user);
      console.log(customer)
      this.accountService.updateAccountCustomer(customer);
      //aqui seria mandado um email para o cliente
      return {
        success: true,
        message: 'Cliente aprovado com sucesso!'
      }
    }
    else{
      return {
        success: false,
        message: 'Ocorreu um erro ao aprovar o cliente.'
      }
    }
  }

  rejectCustomer(customer: Cliente, manager: Gerente, rejectionReason: string){

    customer.statusConta.status = false;
    customer.statusConta.motivo = rejectionReason;
    customer.statusConta.dataAvaliacao = new Date();
    customer.statusConta.gerenteAvaliador = manager;
    this.customerService.updateClient(customer);
    this.accountService.updateAccountCustomer(customer);
    
    //Aqui seria mandado um e-mail para o cliente
    console.log(rejectionReason);
  }
}
