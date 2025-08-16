export class Endereco {
    constructor(
        public tipo: string = '',
        public logradouro: string = '',
        public numero: number = 0,
        public complemento: string = '',
        public cep: string = '',
        public cidade: string = '',
        public estado: string = ''
    ){}
}
