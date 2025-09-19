import { TipoUsuario } from "../enums/TipoUsuario";

export class User {

    constructor(
        public tipoUsuario: TipoUsuario = TipoUsuario.CLIENTE,
        public login: string = '',
        public senha: string = '',
        public idPerfil: number = 0
    ){}
}
