import { Injectable } from '@angular/core';
import { Cliente } from '../../shared/models/cliente.model';

const LS_CHAVE = "clientes";

export interface SaveResult {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  constructor() { }

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

  saveClient(newClient: Cliente): SaveResult{
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
  updateClient(updatedClient: Cliente):SaveResult{
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

    //Verifica se o salário foi alterado
    if(updatedClient.salario !== customers[index].salario){

      //Placeholder para calculo de novo limite, deve ser movido para o gerente?
      //O Cliente tem direito a limite se o salario for >= R$2000,00
      //O limite do Cliente é igual a metade do seu salario
      if (updatedClient.salario >= 2000){
        let novoLimite = updatedClient.salario * 0.5
        
        if (novoLimite < updatedClient.saldo) {
          novoLimite = updatedClient.saldo; //Ajusta limite ao saldo negativo
        }
        updatedClient.limite = novoLimite;
      }
      
    } else {
      //Se o salário não mudou o limite permanece o mesmo
      updatedClient.limite = customers[index].limite; 
    }

    customers[index] = updatedClient;
    localStorage[LS_CHAVE] = JSON.stringify(customers);

    return {
      success: true,
      message: 'Cliente atualizado com sucesso!'
    };

  }
  
}
