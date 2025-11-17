export class ContaTransferenciaResponse {
    
    constructor(
        public conta: string = '',
        public data: Date = new Date(),
        public destino: string = '',
        public saldo: number = 0,
        public valor: number = 0
    ){}
}