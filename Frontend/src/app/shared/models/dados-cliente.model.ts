export class DadoCliente {

constructor(
    public cpf: string = '',
    public nome: string = '',
    public email: string = '',
    public endereco: string = '',
    public cidade: string = '',
    public estado: string = '',
    public cep: string = '',
    public telefone: string = '',
    public salario: number = 0,
    public limite: number = 0,
    public conta: string = '',
    public gerente: string = '',
    public saldo: number = 0,
    public gerente_nome: string = '',
    public gerente_email: string = '',
  ){}
}