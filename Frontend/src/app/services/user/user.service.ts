import {inject, Injectable} from '@angular/core';
import {LoginResponse} from "../../shared/models/loginResponse.model";
import {LoginRequest} from "../../shared/models/loginRequest.model";
import AxiosService from "../axios/axios.service";
import {LoginInfo} from "../../shared/models/login-info.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly axiosService = inject(AxiosService);

  constructor() { }

  public login(login: LoginRequest): Promise<LoginResponse> {
    return this.axiosService.post<LoginResponse>("/login", login);
  }

  public buscarDadosUsuario(email: string): Promise<LoginInfo> {
    return this.axiosService.get<LoginInfo>(`/${email}`);
  }

  public async logout(): Promise<void>{
    const result = await this.axiosService.post<void>('/logout', {});
    sessionStorage.removeItem('cpf');
    sessionStorage.removeItem('token');
    return result;
  }

  public isLogged(): boolean {
    if(sessionStorage.getItem('token')){
      return true;
    }
    else{
      return false;
    }
  }

  public getCpfUsuario(): string {
    return sessionStorage.getItem('cpf') || '';
  }

}
