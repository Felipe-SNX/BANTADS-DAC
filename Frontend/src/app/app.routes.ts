import { Routes } from '@angular/router';
import { AutocadastroComponent } from './pages/autocadastro';
import { AtualizarCadastroComponent } from './pages/atualizar-cadastro/atualizar-cadastro.component';
import { ConsultaExtratoComponent } from './pages/consulta-extrato/consulta-extrato.component';

export const routes: Routes = [
    { path: 'autocadastro', component: AutocadastroComponent},
    { path: 'atualizarcadastro', component: AtualizarCadastroComponent},
    { path: 'consultaExtrato/:id', component: ConsultaExtratoComponent}
];
