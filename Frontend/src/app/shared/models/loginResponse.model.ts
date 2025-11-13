import {TipoUsuario} from "../enums/TipoUsuario";
import {User} from "./user.model";

export class LoginResponse {

  constructor(
    public access_token: string = '',
    public token_type: string = '',
    public tipo: TipoUsuario = TipoUsuario.CLIENTE,
    public usuario: User = new User()
  ){}
}
