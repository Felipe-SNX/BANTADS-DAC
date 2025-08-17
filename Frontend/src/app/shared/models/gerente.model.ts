import { Cliente } from "./cliente.model";

export class Gerente {

    constructor(
        public id: number = 0,
        public nome: string = '',
        public email: string = '',
        public cpf: string = '',
        public telefone: string = '',
        public clientes: Cliente[] = []
    ){}

}
