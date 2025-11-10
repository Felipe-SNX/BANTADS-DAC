import {TipoUsuario} from "../enums/TipoUsuario";

export class User {

    constructor(
        public tipoUsuario: TipoUsuario = TipoUsuario.CLIENTE,
        public login: string = '',
        public senha: string = '',
        public cpf: string = '',
        public id: number = 0
    ){}
}
