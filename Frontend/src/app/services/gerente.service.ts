import { Injectable } from '@angular/core';
import { Gerente } from '../shared/models/gerente.model';

const LS_CHAVE = "gerentes";

@Injectable({
  providedIn: 'root'
})
export class GerenteService {

  constructor() { }

  listManagers(): Gerente[] {
    const managers = localStorage[LS_CHAVE];
    return managers ? JSON.parse(managers) : [];
  }
  
}
