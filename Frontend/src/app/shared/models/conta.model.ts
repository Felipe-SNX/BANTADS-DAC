import { Cliente } from "./cliente.model";
import { Gerente } from "./gerente.model";

export class Conta {

    constructor(
        public numConta: number = 0,
        public cliente: Cliente = new Cliente(),
        public dataCriacao?: Date,
        public saldo: number = 0,
        public limite: number = 0,
        public gerente: Gerente = new Gerente(),
    ){}
}
