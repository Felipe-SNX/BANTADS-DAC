import { inject, Injectable } from '@angular/core';
import { Gerente } from '../../shared/models/gerente.model';
import { Cliente } from '../../shared/models/cliente.model';
import { ContaService } from '../conta/conta.service';
import { LocalStorageResult } from '../../shared/utils/LocalStorageResult';
import { UserService } from '../user/user.service';
import { User } from '../../shared/models/user.model';
import { TipoUsuario } from '../../shared/enums/TipoUsuario';
import { ClienteService } from '../cliente/cliente.service';
import AxiosService from '../axios/axios.service';
import { DadoGerente } from '../../shared/models/dado-gerente.model';
import { DadoGerenteAtualizacao } from '../../shared/models/dado-gerente-atualizacao.model';
import { GerentesResponse } from '../../shared/models/gerentes-response.model';
import { Dashboard } from '../../shared/models/dashboard.model';
import {DadoGerenteInsercao} from "../../shared/models/dado-gerente-insercao.model";

@Injectable({
  providedIn: 'root'
})
export class GerenteService {

  private readonly axiosService = inject(AxiosService);

  constructor(
    private readonly accountService: ContaService,
    private readonly userService: UserService,
    private readonly customerService: ClienteService
  ) { }

  public listarGerentes(): Promise<DadoGerente[]> {
    return this.axiosService.get<DadoGerente[]>("/gerentes");
  }

  public dashboardAdmin(): Promise<Dashboard[]> {
    return this.axiosService.get<Dashboard[]>("/gerentes?filtro=dashboard");
  }

  public getGerente(cpf: string): Promise<DadoGerente>{
    return this.axiosService.get<DadoGerente>(`/gerentes/${cpf}`);
  }

  public saveGerente(dadoGerenteInsercao: DadoGerenteInsercao): Promise<DadoGerenteInsercao>{
    return this.axiosService.post<DadoGerenteInsercao>("/gerentes", dadoGerenteInsercao);
  }

  public updateGerente(dadoGerenteAtualizacao: DadoGerenteAtualizacao): Promise<DadoGerenteAtualizacao>{
    return this.axiosService.put<DadoGerenteAtualizacao>("/gerentes", dadoGerenteAtualizacao);
  }

  public updateManager(dadoGerenteAtualizacao: DadoGerenteAtualizacao, cpf: string): Promise<GerentesResponse[]> {
    return this.axiosService.put<GerentesResponse[]>(`/gerentes/${cpf}`, dadoGerenteAtualizacao);
  }

  public deleteManager(cpf: string): Promise<void>{
    return this.axiosService.delete<void>(`/gerentes/${cpf}`);
  }

}
