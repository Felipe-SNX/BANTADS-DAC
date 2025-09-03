import { Routes } from '@angular/router';
import { AutocadastroComponent } from './pages/autocadastro';
import { AtualizarCadastroComponent } from './pages/atualizar-cadastro/atualizar-cadastro.component';
import { ConsultaExtratoComponent } from './pages/consulta-extrato/consulta-extrato.component';
import { TelaInicialClienteComponent } from './pages/tela-inicial-cliente/tela-inicial-cliente.component';
import { LoginComponent } from './pages/login/login.component';
import { TransferenciaComponent } from './pages/transferencia/transferencia.component';

export const routes: Routes = [
    { path: '', component: LoginComponent},
    { path: 'autocadastro', component: AutocadastroComponent},
    { path: 'atualizarcadastro', component: AtualizarCadastroComponent},
    { path: 'consultaExtrato/:id', component: ConsultaExtratoComponent},
    { path: 'cliente/:id', component: TelaInicialClienteComponent},
    { path: 'transferencia/:id', component: TransferenciaComponent},
];
