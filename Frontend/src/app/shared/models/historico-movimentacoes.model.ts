import { TipoMovimentacao } from "../enums/tipoMovimentacao";
import { Cliente } from "./cliente.model";

export class HistoricoMovimentacoes {
    
    constructor(
        public data?: Date,
        public tipo?: TipoMovimentacao,
        public clienteOrigem: Cliente = new Cliente(),
        public clienteDestino: Cliente | null = null,
        public valor: number = 0,
    ){}
}
