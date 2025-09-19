import { Gerente } from "./gerente.model";

export class StatusConta {
    constructor(
        public status: boolean = false,
        public motivo: string | null = null,
        public dataAvaliacao: Date | null = null,
        public gerenteAvaliador: Gerente | null = null
    ){}
}
