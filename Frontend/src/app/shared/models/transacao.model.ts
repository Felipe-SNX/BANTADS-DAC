import { TipoMovimentacao } from "../enums/TipoMovimentacao";
import { Cliente } from "./cliente.model";

export class Transacao {
    
    constructor(
        public data: Date,
        public tipo: TipoMovimentacao,
        public clienteOrigem: Cliente | null = null,
        public clienteDestino: Cliente | null = null,
        public valor: number = 0,
    ){}
}
