import { ItemExtratoResponse } from "./item-extrato-response.model";

export class ExtratoResponse {

  constructor(
    public conta: string = '',
    public saldo: number = 0,
    public movimentacoes: ItemExtratoResponse[] = []   
  ){}

}