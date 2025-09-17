import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective } from 'ngx-mask';
import { UserService } from '../../../services/auth/user.service';
import { ContaService } from '../../../services/conta/conta.service';
import { TransacaoService } from '../../../services/transacao/transacao.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { TipoMovimentacao } from '../../../shared/enums/TipoMovimentacao';
import { Cliente } from '../../../shared/models/cliente.model';
import { Conta } from '../../../shared/models/conta.model';
import { Transacao } from '../../../shared/models/transacao.model';
import { User } from '../../../shared/models/user.model';

@Component({
  selector: 'app-deposito',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxMaskDirective, SidebarComponent],
  templateUrl: './deposito.component.html',
  styleUrl: './deposito.component.css'
})
export class DepositoComponent implements OnInit{

  @ViewChild('depositoForm') depositoForm!: NgForm;
  user: User | null | undefined;
  loading: boolean = false;
  private readonly toastr = inject(ToastrService);

  conta: Conta | undefined;

  valor: string = ''
  
  onActionSelected(action: string) {    
  }

  constructor(
    private readonly accountService: ContaService, 
    private readonly transactionService: TransacaoService,
    private readonly router: Router,
    private readonly cd: ChangeDetectorRef,
    private readonly userService: UserService
  ){
  }

  ngOnInit(): void {
    const temp = this.userService.findLoggedUser();

    if(!temp) this.router.navigate(['/']);

    this.user = temp; 

    const tempAccount = this.accountService.getAccountByCustomer(this.user?.usuario as Cliente);

    if(!tempAccount){
      this.router.navigate(['/']);
    }
    else{
      this.conta = tempAccount;
    }
  }

  onSubmit() {
    Object.values(this.depositoForm.controls).forEach(control => {
      control.markAsTouched();
    });

    if (this.depositoForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    this.loading = true;
    this.cd.detectChanges();

    setTimeout(() => {
      try {
  
        const valor = +this.valor;
        const transacao = new Transacao(new Date(), TipoMovimentacao.DEPOSITO, this.user?.usuario as Cliente, null, valor);
        const result = this.transactionService.registerNewTransaction(transacao);
        
        if (result.success) {
          this.toastr.success('Valor depositado com sucesso', 'Sucesso');
          console.log("Depósito efetuado com sucesso");
          this.router.navigate(['cliente/', this.user?.usuario?.id])
        } else {
          this.toastr.error(result.message, 'Erro');
        }

      } catch (error) {
        console.log(error);
        this.toastr.error('Por favor, tente novamente', 'Erro');
      } finally {
        this.loading = false;
        this.cd.detectChanges();
      }
    }, 0); 

  }
}

