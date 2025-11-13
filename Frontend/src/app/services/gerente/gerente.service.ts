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

const LS_CHAVE = "gerentes";

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
    const urlMontada = `/gerentes/${cpf}`;
    console.warn('1. GerenteService montou a URL:', urlMontada);
    return this.axiosService.get<DadoGerente>(`/gerentes/${cpf}`);
  }

  public saveGerente(dadoGerenteInsercao: DadoGerenteInsercao): Promise<DadoGerenteInsercao>{
    return this.axiosService.post<DadoGerenteInsercao>("/gerentes", dadoGerenteInsercao);
  }

  public updateGerente(dadoGerenteAtualizacao: DadoGerenteAtualizacao): Promise<DadoGerenteAtualizacao>{
    return this.axiosService.put<DadoGerenteAtualizacao>("/gerentes", dadoGerenteAtualizacao);
  }

  listManagers(): Gerente[] {
    const managers = localStorage[LS_CHAVE];
    return managers ? JSON.parse(managers) : [];
  }

  updateManager(dadoGerenteAtualizacao: DadoGerenteAtualizacao, cpf: string): Promise<GerentesResponse[]> {
    return this.axiosService.put<GerentesResponse[]>(`/gerentes/${cpf}`, dadoGerenteAtualizacao);
  }

  deleteManager(cpf: string): Promise<void>{
    return this.axiosService.delete<void>(`/gerentes/${cpf}`);
  }

  listManagerById(id: number): Gerente | undefined {
    const idNumerico = Number(id);
    const managers: Gerente[] = this.listManagers();
    const manager: Gerente | undefined = managers.find((currentManager) => currentManager.id === idNumerico)
    return manager;
  }

  listCustomersForApprove(manager: Gerente){
    const customers = this.customerService.listClient();

    const filteredCustomers = customers.filter((customer) => {
      if(customer.gerente.id === manager.id && !customer.statusConta.status && customer.statusConta.motivo === null){
        return customer;
      }
      else{
        return;
      }
    })

    return filteredCustomers;
  }

  createManager(manager: Gerente): LocalStorageResult {
    const managers = this.listManagers();

    const checkManager = managers.find((currentManager) => currentManager.cpf === manager.cpf);

    if(checkManager){
      return {
        success: false,
        message: `Erro: O CPF ${manager.cpf} já está cadastrado.`
      };
    }

    this.balanceCustomersToNewManager(manager, managers);

    managers.push(manager);
    localStorage[LS_CHAVE] = JSON.stringify(managers);

    return {
      success: true,
      message: 'Gerente cadastrado com sucesso!'
    };
  }

  private balanceCustomersToNewManager(newManager: Gerente, managers: Gerente[]){

    if(managers.length === 0 || (managers.length === 1 && managers[0].clientes.length === 1)){
      return;
    }

    const chosenManager = managers.reduce((managerMoreCustomers, currentManager) => {
      if(managerMoreCustomers.clientes.length < currentManager.clientes.length){
        return currentManager;
      }
      else if(managerMoreCustomers.clientes.length === currentManager.clientes.length){
        return this.calculateManagerBalance(managerMoreCustomers, currentManager);
      }
      else{
        return managerMoreCustomers;
      }
    });

    if(chosenManager.clientes.length === 1) return;

    this.moveCustomerToNewManager(newManager, chosenManager);
  }

  private moveCustomerToNewManager(newManager: Gerente, oldManager: Gerente){
    const customer = oldManager.clientes.pop();
    newManager.clientes.push(customer!);
  }

  private calculateManagerBalance(manager1: Gerente, manager2: Gerente): Gerente{
    const manager1Balance = this.calculateManagerTotalBalance(manager1);
    const manager2Balance = this.calculateManagerTotalBalance(manager2);

    if(manager1Balance < manager2Balance){
      return manager1;
    }
    else {
      return manager2;
    }
  }

  private calculateManagerTotalBalance(manager: Gerente){
    let total = 0;

    manager.clientes.forEach((currentCustomer) => {
      const customerBalance = this.accountService.getAccountByCustomer(currentCustomer);
      if(customerBalance) total += customerBalance.saldo;
    });

    return total;
  }


  addCustomerToManager(customer: Cliente): Gerente {
    const managers = this.listManagers();

    const chosenManager = managers.reduce((managerLessCustomers, currentManager) => {
      if (currentManager.clientes.length < managerLessCustomers.clientes.length) {
        return currentManager;
      } else {
        return managerLessCustomers;
      }
    });

    //Necessário criar um novo array para que reflita a alteração no local storage se não o angular não vê necessidade em alterar
    const updatedManagers = managers.map(manager => {
      if (manager.id === chosenManager.id) {
        return {
          ...manager,
          clientes: [...manager.clientes, customer]
        };
      }
      return manager;
    });

    localStorage.setItem(LS_CHAVE, JSON.stringify(updatedManagers));
    return chosenManager;
  }

  approveCustomer(customer: Cliente, manager: Gerente): LocalStorageResult{

    customer.statusConta.status = true;
    customer.statusConta.motivo = null;
    customer.statusConta.dataAvaliacao = new Date();
    customer.statusConta.gerenteAvaliador = manager;
    this.customerService.updateClient(customer);

    const result = this.accountService.createAccount(customer, manager);

    if(result.success){
      const user: User = new User(TipoUsuario.CLIENTE, customer.email, customer.cpf, customer.id.toString());
      this.userService.createUserAccount(user);
      console.log(customer)
      this.accountService.updateAccountCustomer(customer);
      //aqui seria mandado um email para o cliente
      return {
        success: true,
        message: 'Cliente aprovado com sucesso!'
      }
    }
    else{
      return {
        success: false,
        message: 'Ocorreu um erro ao aprovar o cliente.'
      }
    }
  }

  rejectCustomer(customer: Cliente, manager: Gerente, rejectionReason: string){

    customer.statusConta.status = false;
    customer.statusConta.motivo = rejectionReason;
    customer.statusConta.dataAvaliacao = new Date();
    customer.statusConta.gerenteAvaliador = manager;
    this.customerService.updateClient(customer);
    this.accountService.updateAccountCustomer(customer);

    //Aqui seria mandado um e-mail para o cliente
    console.log(rejectionReason);
  }

  findLoggedUser(): number | undefined{
    const gerenteString = sessionStorage.getItem('usuarioLogado');
    console.log(gerenteString);
    if (gerenteString) {
      const usuarioLogado = JSON.parse(gerenteString);
      return usuarioLogado.idPerfil;
    }
    else{
      return undefined;
    }
  }
}
