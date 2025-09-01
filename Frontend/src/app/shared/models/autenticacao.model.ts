import { TipoUsuario } from "../enums/TipoUsuario";

export class Autenticacao {

    constructor(
        public nome: string = '',
        public tipoUsuario: TipoUsuario,
        public login: string,
        public senha: string
    ){}
}
