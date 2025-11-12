export class DadoConta {

  constructor(
    public cliente: string = '',
    public conta: string = '',
    public saldo: number = 0,
    public limite: number = 0,
    public gerente: string = '',
    public dataCriacao: Date = new Date()
  ){}
}
