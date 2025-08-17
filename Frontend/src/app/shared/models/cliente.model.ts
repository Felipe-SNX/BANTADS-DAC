import { Endereco } from "./endereco.model";

export class Cliente {

    constructor(
        public id: number = 0,
        public nome: string = '',
        public email: string = '',
        public cpf: string = '',
        public endereco: Endereco = new Endereco(),
        public telefone: string = '',
        public salario: number = 0
    ){}
}
