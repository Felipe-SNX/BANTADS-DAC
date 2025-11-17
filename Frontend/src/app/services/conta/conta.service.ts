import { inject, Injectable } from '@angular/core';
import AxiosService from '../axios/axios.service';
import { ContaDepositoResponse } from '../../shared/models/conta-deposito-response.model';
import { ContaDepositoRequest } from '../../shared/models/conta-deposito-request.model';
import { ContaTransferenciaRequest } from '../../shared/models/conta-transferencia-request.model';
import { ContaTransferenciaResponse } from '../../shared/models/conta-transferencia-response.model';
import { ExtratoResponse } from '../../shared/models/extrato-response.model';

@Injectable({
  providedIn: 'root'
})
export class ContaService {

  private readonly axiosService = inject(AxiosService); 

  constructor() { }

  public depositarConta(numConta:string, contaDepositoRequest: ContaDepositoRequest): Promise<ContaDepositoResponse> { 
      return this.axiosService.post<ContaDepositoResponse>(`/contas/${numConta}/depositar`, contaDepositoRequest);
  }

  public sacarConta(numConta:string, contaDepositoRequest: ContaDepositoRequest): Promise<ContaDepositoResponse> { 
      return this.axiosService.post<ContaDepositoResponse>(`/contas/${numConta}/sacar`, contaDepositoRequest);
  }

  public transferirEntreContas(numConta:string, contaTransferenciaRequest: ContaTransferenciaRequest): Promise<ContaTransferenciaResponse> { 
      return this.axiosService.post<ContaTransferenciaResponse>(`/contas/${numConta}/transferir`, contaTransferenciaRequest);
  }

  public extratoConta(numConta:string): Promise<ExtratoResponse> { 
      return this.axiosService.get<ExtratoResponse>(`/contas/${numConta}/extrato`);
  }

}

