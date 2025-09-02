import { Injectable } from '@angular/core';
import { User } from '../../shared/models/user.model';

const LS_CHAVE = "users";

export interface SaveResult {
  success: boolean;
  message: string;
}


@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor() { }

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
}
