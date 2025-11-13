import {TipoUsuario} from "../enums/TipoUsuario";

export class DadoGerente {

  constructor(
    public cpf: string = '',
    public nome: string = '',
    public telefone: string = '',
    public email: string = '',
    public tipo: TipoUsuario = TipoUsuario.GERENTE,
  ){}

}
