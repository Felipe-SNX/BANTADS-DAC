import {inject, Injectable} from '@angular/core';
import { User } from '../../shared/models/user.model';
import { LocalStorageResult } from '../../shared/utils/LocalStorageResult';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {LoginResponse} from "../../shared/models/loginResponse.model";
import {LoginRequest} from "../../shared/models/loginRequest.model";
import AxiosService from "../axios/axios.service";
import {LoginInfo} from "../../shared/models/login-info.model";

const LS_CHAVE = "users";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private axiosService = inject(AxiosService);

  constructor() { }

  public login(login: LoginRequest): Promise<LoginResponse> {
    return this.axiosService.post<LoginResponse>("/login", login);
  }

  public buscarDadosUsuario(email: String): Promise<LoginInfo> {
    return this.axiosService.get<LoginInfo>(`/${email}`);
  }

  public async logout(): Promise<void>{
    const result = await this.axiosService.post<void>('/logout', {});
    sessionStorage.removeItem('cpf');
    sessionStorage.removeItem('token');
    return result;
  }

  listUsers(): User[] {
    const users = localStorage[LS_CHAVE];
    return users ? JSON.parse(users) : [];
  }

  isLogged(): boolean {
    if(sessionStorage.getItem('token')){
      return true;
    }
    else{
      return false;
    }
  }

  getCpfUsuario(): string {
    return sessionStorage.getItem('cpf') || '';
  }



  createUserAccount(user: User): LocalStorageResult{
    const users = this.listUsers();
    const checkUser = users.find((currentUser) => currentUser.login === user.login);

    if(checkUser){
      return {
        success: false,
        message: `Erro: O login ${user.login} já está cadastrado.`
      }
    }

    users.push(user);
    localStorage[LS_CHAVE] = JSON.stringify(users);

    return {
      success: true,
      message: 'Usuário cadastrado com sucesso!'
    }
  }

  findUserByLogin(login: string): User | undefined {
    const users: User[] = this.listUsers();
    const user: User | undefined = users.find((currentUser) => currentUser.login === login)
    return user;
  }

  getUserByLoginAndPassword(login: string, password: string): User | undefined {
    const users: User[] = this.listUsers();
    const user: User | undefined = users.find((currentUser) => currentUser.login === login && currentUser.senha === password)
    return user;
  }

  findLoggedUser(): User | undefined{
    const usuarioString = sessionStorage.getItem('usuarioLogado');

    if (usuarioString) {
      const usuarioLogado = JSON.parse(usuarioString);
      return usuarioLogado as User;
    }
    else{
      return undefined;
    }
  }

  updateLoggedUser(userLogged: User | null | undefined): LocalStorageResult{
    if(userLogged === null || userLogged === undefined) return {success: false, message: 'O Usuário não pode ser vazio'};

    sessionStorage.removeItem('usuarioLogado');
    console.log('apagando user')

    const users: User[] = this.listUsers();
    const user: User | undefined = users.find((currentUser) => currentUser.login === userLogged?.login
                                                            && currentUser.id === userLogged?.id
                                                            && currentUser.tipoUsuario === userLogged?.tipoUsuario)

    if(user){
      console.log('setando user')
      sessionStorage['usuarioLogado'] = JSON.stringify(user);
      return {success: true, message: 'Usuario atualizado com sucesso'}
    }
    else{
      return {success: false, message: 'Erro ao atualizar o usuário'}
    }
  }

  updateUserPassword(user: User, newPassword: string): LocalStorageResult{
    const users = this.listUsers();
    const index = users.findIndex((currentUser) => currentUser.login === user.login);

    if(index === -1){
      return {
        success: false,
        message: `Erro: Não foi encontrado nenhum usuário com o login ${user.login}`
      }
    }

    users[index].senha = newPassword;
    localStorage[LS_CHAVE] = JSON.stringify(users)

    return {
      success: true,
      message: 'Senha atualizada com sucesso!'
    }
  }
}
