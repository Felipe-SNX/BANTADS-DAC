export class AutocadastroModel {

    constructor(
        public cpf: string = '',
        public email: string = '',
        public nome: string = '',
        public salario: number = 0,
        public telefone: string = '',
        public endereco: string = '',
        public cep: string = '',
        public cidade: string = '',
        public estado: string = ''  
    ){}
}
