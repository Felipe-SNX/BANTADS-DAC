import { TipoMovimentacao } from "../enums/TipoMovimentacao";

export class ItemExtratoResponse {

  constructor(
    public data: Date = new Date(),
    public tipo: TipoMovimentacao = TipoMovimentacao.saldo,
    public origem: string = '',
    public destino: string = '',
    public valor: number = 0    
  ){}

}