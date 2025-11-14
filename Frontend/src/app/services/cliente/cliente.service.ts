import {inject, Injectable} from '@angular/core';
import { Cliente } from '../../shared/models/cliente.model';
import { ContaService } from '../conta/conta.service';
import { LocalStorageResult } from '../../shared/utils/LocalStorageResult';
import { Gerente } from '../../shared/models/gerente.model';
import AxiosService from "../axios/axios.service";
import {ClienteRelatorioResponse} from "../../shared/models/cliente-relatorio-response.model";
import { ClienteResponse } from '../../shared/models/cliente-response.model';
import { ClienteAprovar } from '../../shared/models/cliente-aprovar.model';
import { ClienteMotivoRejeicao } from '../../shared/models/cliente-motivo-rejeicao.model';
import { DadoCliente } from '../../shared/models/dados-cliente.model';
import { AutocadastroModel } from '../../shared/models/autocadastro.model';
import { PerfilInfo } from '../../shared/models/perfil-info.model';

const LS_CHAVE = "clientes";

export interface ClientData {
  cpfCliente: string;
  nomeCliente: string;
  emailCliente: string;
  salario: number;
  numeroConta: string;
  saldo: number;
  limiteCliente: number;
  cpfGerente: string;
  nomeGerente: string;
}

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

  listClientData(): ClientData[] {
    const clientes = this.listClient();
    const contas = JSON.parse(localStorage.getItem('contas') || '[]');
    const gerentes: Gerente[] = JSON.parse(localStorage.getItem('gerentes') || '[]');

    const allClientData: ClientData[] = [];

    clientes.forEach(cliente => {
      const conta = contas.find((c: any) => c.cliente.id === cliente.id);
      const gerente = gerentes.find(g => g.id === cliente.gerente.id);

      if (conta && gerente) {
        allClientData.push({
          cpfCliente: cliente.cpf,
          nomeCliente: cliente.nome,
          emailCliente: cliente.email,
          salario: cliente.salario,
          numeroConta: conta.numero,
          saldo: conta.saldo,
          limiteCliente: conta.limite,
          cpfGerente: gerente.cpf,
          nomeGerente: gerente.nome
        });
      }
    });

    allClientData.sort((a, b) => a.nomeCliente.localeCompare(b.nomeCliente));

    return allClientData;
  }

  getClientByCpf(cpf: string): Cliente | undefined {
    const customers: Cliente[] = this.listClient();
    const customer: Cliente | undefined = customers.find((currentCustomer) => currentCustomer.cpf === cpf)
    return customer;
  }

}
