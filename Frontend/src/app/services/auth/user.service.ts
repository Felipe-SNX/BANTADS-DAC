import { Injectable } from '@angular/core';
import { User } from '../../shared/models/user.model';
import { ContaService } from '../conta/conta.service';
import { Conta } from '../../shared/models/conta.model';
import { Cliente } from '../../shared/models/cliente.model';

const LS_CHAVE = "users";

export interface SaveResult {
  success: boolean;
  message: string;
}


@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private readonly accountService: ContaService) { }

  //Método usado inicialmente apenas para o localStorage
  listUsers(): User[] {
    const users = localStorage[LS_CHAVE];
    return users ? JSON.parse(users) : [];
  }
  
  getUserByLoginAndPassword(login: string, password: string): User | undefined {
    const users: User[] = this.listUsers();
    const user: User | undefined = users.find((currentUser) => currentUser.login === login && currentUser.senha === password)
    return user;
  }

  findLoggedUser(): User | undefined{
    const usuarioString = localStorage.getItem('usuarioLogado');
    
    if (usuarioString) {
      const usuarioLogado = JSON.parse(usuarioString);
      return usuarioLogado as User;
    }
    else{
      return undefined;
    }
  }

  updateUserCustomerData(user: User | null | undefined, customer: Cliente): SaveResult{
    if(user === null || user === undefined) return {success: false, message: 'O Usuário não pode ser vazio'};

    const users = this.listUsers();
    const index = users.findIndex((currentUser) => currentUser.usuario?.id === user.usuario?.id);

    users[index].usuario = customer;

    localStorage[LS_CHAVE] = JSON.stringify(users);

    return {success: true, message: "Usuario atualizado"}
  }

  updateLoggedUser(userLogged: User | null | undefined): SaveResult{
    if(userLogged === null || userLogged === undefined) return {success: false, message: 'O Usuário não pode ser vazio'};

    localStorage.removeItem('usuarioLogado');
    console.log('apagando user')

    const users: User[] = this.listUsers();
    const user: User | undefined = users.find((currentUser) => currentUser.login === userLogged?.login && currentUser.usuario?.id === userLogged?.usuario?.id)
    
    if(user){
      console.log('setando user')
      localStorage['usuarioLogado'] = JSON.stringify(user);
      return {success: true, message: 'Usuario atualizado com sucesso'}
    }
    else{
      return {success: false, message: 'Erro ao atualizar o usuário'}
    }
  }

  deleteLoggedUser(){
    localStorage.removeItem('usuarioLogado');
  }
}
