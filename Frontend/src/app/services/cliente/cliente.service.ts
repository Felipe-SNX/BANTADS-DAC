import {inject, Injectable} from '@angular/core';
import { ContaService } from '../conta/conta.service';
import AxiosService from "../axios/axios.service";
import {ClienteRelatorioResponse} from "../../shared/models/cliente-relatorio-response.model";
import { ClienteResponse } from '../../shared/models/cliente-response.model';
import { ClienteAprovar } from '../../shared/models/cliente-aprovar.model';
import { ClienteMotivoRejeicao } from '../../shared/models/cliente-motivo-rejeicao.model';
import { DadoCliente } from '../../shared/models/dados-cliente.model';
import { AutocadastroModel } from '../../shared/models/autocadastro.model';
import { PerfilInfo } from '../../shared/models/perfil-info.model';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private readonly axiosService = inject(AxiosService);

  constructor(private readonly accountService: ContaService) { }

  public buscarClientes(): Promise<ClienteResponse[]> {
    return this.axiosService.get<ClienteResponse[]>("/clientes");
  }

  public buscarTop3Clientes(): Promise<ClienteResponse[]> {
    return this.axiosService.get<ClienteResponse[]>("/clientes?filtro=melhores_clientes");
  }

  public relatorioClientes(): Promise<ClienteRelatorioResponse[]> {
    return this.axiosService.get<ClienteRelatorioResponse[]>("/clientes?filtro=adm_relatorio_clientes");
  }

  public clientesParaAprovar(): Promise<ClienteResponse[]> {
    return this.axiosService.get<ClienteResponse[]>("/clientes?filtro=para_aprovar");
  }

  public cadastrarCliente(autocadastroModel: AutocadastroModel): Promise<AutocadastroModel> {
    return this.axiosService.post<AutocadastroModel>("/clientes", autocadastroModel);
  }

  public atualizarCliente(perfilInfo: PerfilInfo, cpf: string): Promise<PerfilInfo> {
    return this.axiosService.put<PerfilInfo>(`/clientes/${cpf}`, perfilInfo);
  }

  public aprovarCliente(clienteParaAprovar: ClienteAprovar, cpf: string): Promise<void> {
    return this.axiosService.post<void>(`/clientes/${cpf}/aprovar`, clienteParaAprovar);
  }

  public rejeitarCliente(clienteMotivoRejeicao: ClienteMotivoRejeicao, cpf: string): Promise<void> {
    return this.axiosService.post<void>(`/clientes/${cpf}/rejeitar`, clienteMotivoRejeicao);
  }

  public getCliente(cpf: string): Promise<DadoCliente> {
    return this.axiosService.get<DadoCliente>(`/clientes/${cpf}`);
  }

}
