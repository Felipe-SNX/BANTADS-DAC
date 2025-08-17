import { Injectable } from '@angular/core';
import { Gerente } from '../../shared/models/gerente.model';
import { Cliente } from '../../shared/models/cliente.model';

const LS_CHAVE = "gerentes";

@Injectable({
  providedIn: 'root'
})
export class GerenteService {

  constructor() { }

  listManagers(): Gerente[] {
    const managers = localStorage[LS_CHAVE];
    return managers ? JSON.parse(managers) : [];
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
}
