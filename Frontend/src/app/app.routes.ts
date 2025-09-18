import { Routes } from '@angular/router';
import { AtualizarCadastroComponent } from './pages/cliente/atualizar-cadastro/atualizar-cadastro.component';
import { LoginComponent } from './pages/login/login.component';
import { InserirGerenteComponent } from './pages/admin/inserir-editar-gerente/inserir-editar-gerente.component';
import { AutocadastroComponent } from './pages/cliente/autocadastro/autocadastro.component';
import { DepositoComponent } from './pages/cliente/deposito/deposito.component';
import { SaqueComponent } from './pages/cliente/saque/saque.component';
import { TransferenciaComponent } from './pages/cliente/transferencia/transferencia.component';
import { ConsultaExtratoComponent } from './pages/cliente/consulta-extrato/consulta-extrato.component';
import { TelaInicialClienteComponent } from './pages/cliente/tela-inicial-cliente/tela-inicial-cliente.component';
import { TelaInicialGerenteComponent } from './pages/gerente/tela-inicial-gerente/tela-inicial-gerente.component';
import { TelaInicialAdminComponent } from './pages/admin/tela-inicial-admin/tela-inicial-admin.component';

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
    { path: 'admin/adicionarGerente', component: InserirGerenteComponent},
    { path: 'admin/editarGerente/:id', component: InserirGerenteComponent},
    { path: 'admin/:id', component: TelaInicialAdminComponent},
];