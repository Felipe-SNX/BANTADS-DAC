import {inject, Injectable} from '@angular/core';
import AxiosService from "../axios/axios.service";
import {Dashboard} from "../../shared/models/dashboard.model";

export interface AdminData {
  id: number;
  nome: string;
  email: string;
  cpf: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private readonly axiosService = inject(AxiosService);

  constructor() { }

  getAdminData(): AdminData | null {
    const adminData = localStorage.getItem('admin');
    if (!adminData) {
      return null;
    }
    const admin = JSON.parse(adminData);
    return {
      id: admin.id,
      nome: admin.nome,
      email: admin.email,
      cpf: admin.cpf
    };
  }

}
