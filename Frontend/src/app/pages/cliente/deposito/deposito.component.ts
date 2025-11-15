import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective } from 'ngx-mask';
import { UserService } from '../../../services/user/user.service';
import { ContaService } from '../../../services/conta/conta.service';
import { TransacaoService } from '../../../services/transacao/transacao.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { TipoMovimentacao } from '../../../shared/enums/TipoMovimentacao';
import { Cliente } from '../../../shared/models/cliente.model';
import { Conta } from '../../../shared/models/conta.model';
import { Transacao } from '../../../shared/models/transacao.model';
import { User } from '../../../shared/models/user.model';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { DadoCliente } from '../../../shared/models/dados-cliente.model';
import { ContaDepositoRequest } from '../../../shared/models/conta-deposito-request.model';

@Component({
  selector: 'app-deposito',
  standalone: true,
  imports: [CommonModule,
            FormsModule,
            NgxMaskDirective,
            SidebarComponent,
            LoadingComponent
          ],
  templateUrl: './deposito.component.html',
  styleUrl: './deposito.component.css'
})
export class DepositoComponent implements OnInit{

  @ViewChild('depositoForm') depositoForm!: NgForm;
  user: User | null | undefined;
  cpf: string = '';
  loading: boolean = false;
  private readonly toastr = inject(ToastrService);
  conta: Conta | undefined;
  customer: DadoCliente | undefined;
  valor: string = ''
  numconta: string = '';

  onActionSelected(action: string) {
  }

  constructor(
    private readonly accountService: ContaService,
    private readonly transactionService: TransacaoService,
    private readonly router: Router,
    private readonly cd: ChangeDetectorRef,
    private readonly userService: UserService,
    private readonly customerService: ClienteService
  ){}

  async ngOnInit(): Promise<void> {
    this.loading = true;
    const user = this.userService.isLogged(); 
    if(user){ //Todo adicionar try-catch
      this.cpf = this.userService.getCpfUsuario(); //procura cpf
      const cliente = await this.customerService.getCliente(this.cpf); //recebe dados cliente
      this.numconta = cliente.conta;   //salvou numero da conta    
    } else {
    this.router.navigate(['/']);
    }
    this.loading = false;
  }



  async onSubmit() {
    Object.values(this.depositoForm.controls).forEach(control => { // marca todos os campos como tocados para exibir mensagens de erro  
      control.markAsTouched();
    });

    if (this.depositoForm.invalid) { // verifica se o formulário é válido 
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    this.loading = true;
    this.cd.detectChanges();

     try {

        const valor = +this.valor; // "+" converte string para number
        const contadepositorequest: ContaDepositoRequest = new ContaDepositoRequest(valor);
        await this.accountService.depositarConta(this.numconta, contadepositorequest);
        
        this.toastr.success('Valor depositado com sucesso', 'Sucesso');
        console.log("Depósito efetuado com sucesso");
        this.router.navigate(['/cliente'])             

      } catch (error) {
        console.log(error);
        this.toastr.error('Por favor, tente novamente', 'Erro');
      } finally {
        this.loading = false;
        this.cd.detectChanges();
      }
   

  }
}

