import { Routes } from '@angular/router';
import { AutocadastroComponent } from './pages/autocadastro';
import { AtualizarClienteComponent } from './pages/atualizar-cliente/atualizar-cliente.component';

export const routes: Routes = [
    { path: 'autocadastro', component: AutocadastroComponent},
    { path: 'atualizarcliente', component: AtualizarClienteComponent}
];
