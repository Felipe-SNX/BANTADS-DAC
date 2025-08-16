import { Injectable } from '@angular/core';
import { Cliente } from '../shared/models/cliente.model';

const LS_CHAVE = "clientes";

export interface SaveResult {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AutocadastroService {

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
}
