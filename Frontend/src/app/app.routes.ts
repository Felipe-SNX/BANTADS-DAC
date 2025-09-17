import { Routes } from '@angular/router';
import { AutocadastroComponent } from './pages/autocadastro';
import { AtualizarCadastroComponent } from './pages/cliente/atualizar-cadastro/atualizar-cadastro.component';
import { ConsultaExtratoComponent } from './pages/consulta-extrato/consulta-extrato.component';
import { TelaInicialClienteComponent } from './pages/tela-inicial-cliente/tela-inicial-cliente.component';
import { LoginComponent } from './pages/login/login.component';
import { TransferenciaComponent } from './pages/transferencia/transferencia.component';
import { TelaInicialGerenteComponent } from './pages/tela-inicial-gerente/tela-inicial-gerente.component';
import { SaqueComponent } from './pages/saque/saque.component';
import { DepositoComponent } from './pages/deposito/deposito.component';
import { TelaInicialAdminComponent } from './pages/tela-inicial-admin/tela-inicial-admin.component';

export const routes: Routes = [
    { path: '', component: LoginComponent},
    { path: 'autocadastro', component: AutocadastroComponent},

    { path: 'cliente/deposito', component: DepositoComponent},
    { path: 'cliente/saque', component: SaqueComponent},
    { path: 'cliente/transferencia', component: TransferenciaComponent},
    { path: 'cliente/atualizarCadastro', component: AtualizarCadastroComponent},    
    { path: 'cliente/consultaExtrato', component: ConsultaExtratoComponent},
    { path: 'cliente', component: TelaInicialClienteComponent},
    { path: 'gerente/:id', component: TelaInicialGerenteComponent},
    { path: 'admin/:id', component: TelaInicialAdminComponent}
];