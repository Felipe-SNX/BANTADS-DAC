export class ClienteResponse {

  constructor(
    public cpf: string = '',
    public nome: string = '',
    public email: string = '',
    public salario: number = 0,
    public endereco: string = '',
    public conta: string = '',
    public gerente: string = '',
    public nomeGerente: string = '',
    public limite: string = '',
    public cidade: string = '',
    public estado: string = '',
    public saldo: number = 0
  ){}
}
