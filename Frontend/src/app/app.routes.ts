import { Routes } from '@angular/router';
import { AutocadastroComponent } from './pages/autocadastro';
import { AtualizarClienteComponent } from './pages/atualizar-cliente/atualizar-cliente.component';
import { ConsultaExtratoComponent } from './pages/consulta-extrato/consulta-extrato.component';

export const routes: Routes = [
    { path: 'autocadastro', component: AutocadastroComponent},
    { path: 'atualizarcliente', component: AtualizarClienteComponent},
    { path: 'consultaExtrato', component: ConsultaExtratoComponent}
];
