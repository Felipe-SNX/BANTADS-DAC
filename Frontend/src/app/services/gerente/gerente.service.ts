import { Injectable } from '@angular/core';
import { Gerente } from '../../shared/models/gerente.model';
import { Cliente } from '../../shared/models/cliente.model';
import { ContaService } from '../conta/conta.service';
import { LocalStorageResult } from '../../shared/utils/LocalStorageResult';

const LS_CHAVE = "gerentes";

@Injectable({
  providedIn: 'root'
})
export class GerenteService {

  constructor(private readonly accountService: ContaService) { }

  listManagers(): Gerente[] {
    const managers = localStorage[LS_CHAVE];
    return managers ? JSON.parse(managers) : [];
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

    managers.push(manager);
    localStorage[LS_CHAVE] = JSON.stringify(managers);

    return {
      success: true,
      message: 'Gerente cadastrado com sucesso!'
    };
  }

  updateManager(manager: Gerente): LocalStorageResult {
    const managers = this.listManagers();

    const checkManager = managers.findIndex((currentManager) => currentManager.cpf === manager.cpf);

    if(!checkManager){
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
      message: 'Gerente cadastrado com sucesso!'
    };
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

  approveCustomer(customer: Cliente, manager: Gerente): void{
    this.accountService.createAccount(customer, manager);
  }

  rejectCustomer(customer: Cliente, rejectionReason: string){
    
  }
}
