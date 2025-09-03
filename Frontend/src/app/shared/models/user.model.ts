import { TipoUsuario } from "../enums/TipoUsuario";
import { Admin } from "./admin.model";
import { Cliente } from "./cliente.model";
import { Gerente } from "./gerente.model";

export class User {

    constructor(
        public tipoUsuario: TipoUsuario = TipoUsuario.CLIENTE,
        public login: string = '',
        public senha: string = '',
        public usuario: Cliente | Gerente | Admin | null = null 
    ){}
}
