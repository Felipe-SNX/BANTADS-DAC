import { Endereco } from "./endereco.model";

export interface Cliente {
    nome: string,
    email: string,
    cpf: string,
    endereco: Endereco,
    telefone: string,
    salario: number
}