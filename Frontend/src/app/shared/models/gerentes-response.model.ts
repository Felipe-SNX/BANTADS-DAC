import { TipoUsuario } from "../enums/TipoUsuario";

export class GerentesResponse {

  constructor(
    public cpf: string = '',
    public nome: string = '',
    public email: string = '',
    public tipo: TipoUsuario = TipoUsuario.GERENTE,
  ){}

}