import { Injectable } from '@angular/core';
import { Cliente } from '../../shared/models/cliente.model';
import { ContaService } from '../conta/conta.service';
import { LocalStorageResult } from '../../shared/utils/LocalStorageResult';

const LS_CHAVE = "clientes";

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  constructor(private readonly accountService: ContaService) { }

  validClient(client: Cliente): boolean{
    const customers = this.listClient();
    const found = customers.find((existClient: Cliente) => existClient.cpf === client.cpf)
    if(found) return false;
    return true;
  }

  //Método usado inicialmente apenas para o localStorage
  listClient(): Cliente[] {
    const customers = localStorage[LS_CHAVE];
    return customers ? JSON.parse(customers) : [];
  }

  getClientById(id: number): Cliente | undefined {
    const customers: Cliente[] = this.listClient();
    const customer: Cliente | undefined = customers.find((currentCustomer) => currentCustomer.id === id)
    return customer;
  }

  saveClient(newClient: Cliente): LocalStorageResult{
    if(!this.validClient(newClient)){
      return {
        success: false,
        message: `Erro: O CPF ${newClient.cpf} já está cadastrado.`
      };
    }

    const customers = this.listClient();
    newClient.id = new Date().getTime();
    customers.push(newClient);
    localStorage[LS_CHAVE] = JSON.stringify(customers);

    return {
      success: true,
      message: 'Cliente cadastrado com sucesso!'
    };
  }
  

  //Método para atualizar dados do perfil Cliente
  updateClient(updatedClient: Cliente): LocalStorageResult{
    let customers = this.listClient();
    const index = customers.findIndex((c:Cliente) => c.id == updatedClient.id);
    
    if(index === -1){
      return{
        success: false,
        message: 'Cliente não encontrado.'
      };
    }

    //Não permite a alteração do CPF
    updatedClient.cpf = customers[index].cpf;
    updatedClient.gerente = customers[index].gerente;

    //Verifica se o salário foi alterado
    if(updatedClient.salario !== customers[index].salario){
      this.accountService.updateAccountLimit(updatedClient);
    }

    customers[index] = updatedClient;
    localStorage[LS_CHAVE] = JSON.stringify(customers);

    return {
      success: true,
      message: 'Cliente atualizado com sucesso!'
    };

  }
  
}
