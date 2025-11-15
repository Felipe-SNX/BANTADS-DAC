export class ContaDepositoResponse {
    
    constructor(
        public conta: string = "",
        public data: Date = new Date(),
        public saldo: number = 0,
    ){}
}