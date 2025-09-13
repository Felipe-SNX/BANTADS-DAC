import { Routes } from '@angular/router';
import { AutocadastroComponent } from './pages/autocadastro';
import { AtualizarCadastroComponent } from './pages/atualizar-cadastro/atualizar-cadastro.component';
import { ConsultaExtratoComponent } from './pages/consulta-extrato/consulta-extrato.component';
import { TelaInicialClienteComponent } from './pages/tela-inicial-cliente/tela-inicial-cliente.component';
import { LoginComponent } from './pages/login/login.component';
import { TransferenciaComponent } from './pages/transferencia/transferencia.component';
import { TelaInicialGerenteComponent } from './pages/tela-inicial-gerente/tela-inicial-gerente.component';
import { SaqueComponent } from './pages/saque/saque.component';
import { DepositoComponent } from './pages/deposito/deposito.component';

export const routes: Routes = [
    { path: '', component: LoginComponent},
    { path: 'autocadastro', component: AutocadastroComponent},
    { path: 'atualizarcadastro/:id', component: AtualizarCadastroComponent},
    
    { path: 'cliente/deposito', component: DepositoComponent},
    { path: 'cliente/saque', component: SaqueComponent},
    { path: 'cliente/transferencia', component: TransferenciaComponent},
    { path: 'cliente/consultaExtrato/:id', component: ConsultaExtratoComponent},
    { path: 'cliente/:id', component: TelaInicialClienteComponent}, 

    { path: 'gerente/:id', component: TelaInicialGerenteComponent},
];