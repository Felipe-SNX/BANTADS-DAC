import {TipoUsuario} from "../enums/TipoUsuario";

export class DadoGerenteInsercao {

  constructor(
    public cpf: string = '',
    public nome: string = '',
    public email: string = '',
    public telefone: string = '',
    public tipo: TipoUsuario = TipoUsuario.GERENTE,
    public senha: string = ''
  ){}
}
