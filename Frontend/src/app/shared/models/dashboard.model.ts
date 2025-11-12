import {DadoGerente} from "./dado-gerente.model";
import {DadoConta} from "./dado-conta.model";

export class Dashboard {

  constructor(
    public gerente: DadoGerente = new DadoGerente(),
    public clientes: DadoConta[] = [],
    public saldo_positivo: number = 0,
    public saldo_negativo: number = 0
  ){}
}
