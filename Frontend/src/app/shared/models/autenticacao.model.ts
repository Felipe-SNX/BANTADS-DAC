import { TipoUsuario } from "../enums/TipoUsuario";

export class Autenticacao {

    constructor(
        public nome: string = '',
        public tipoUsuario: TipoUsuario = TipoUsuario.CLIENTE,
        public login: string = '',
        public senha: string = ''
    ){}
}
