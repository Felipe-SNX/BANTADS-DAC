import { TipoUsuario } from "../enums/TipoUsuario";

export class User {

    constructor(
        public nome: string = '',
        public tipoUsuario: TipoUsuario = TipoUsuario.CLIENTE,
        public login: string = '',
        public senha: string = ''
    ){}
}
