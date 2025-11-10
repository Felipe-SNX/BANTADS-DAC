import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
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

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxMaskDirective, SidebarComponent],
  templateUrl: './transferencia.component.html',
  styleUrl: './transferencia.component.css'
})
export class TransferenciaComponent implements OnInit{
  @ViewChild('transferForm') transferForm!: NgForm;
  user: User | null | undefined;
  loading: boolean = false;
  private readonly toastr = inject(ToastrService);

  public saldo: number = 0;
  public saldoVisivel: boolean = false;

  contaOrigem: Conta | undefined;
  clienteOrigem: Cliente | undefined;

  public transferencia = {
    contaDestino: "",
    valor: ""
  }

  onActionSelected(action: string) {
  }

  constructor(
    private readonly accountService: ContaService,
    private readonly transactionService: TransacaoService,
    private readonly router: Router,
    private readonly cd: ChangeDetectorRef,
    private readonly userService: UserService,
    private readonly customerService: ClienteService
  ){
  }
  ngOnInit(): void {
    const temp = this.userService.findLoggedUser();

    if(!temp) this.router.navigate(['/']);

    this.user = temp;

    const tempCliente = this.customerService.getClientById(this.user?.id as number);
    const tempAccount = this.accountService.getAccountByCustomer(tempCliente as Cliente);

    if(!tempAccount){
      this.router.navigate(['/']);
    }
    else{
      this.clienteOrigem = tempCliente
      this.contaOrigem = tempAccount;
      this.saldo = this.contaOrigem.saldo;
    }
  }

  toggleVisibilidadeSaldo(): void {
    this.saldoVisivel = !this.saldoVisivel;
  }

  onSubmit() {
    Object.values(this.transferForm.controls).forEach(control => {
      control.markAsTouched();
    });

    if (this.transferForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    this.loading = true;
    this.cd.detectChanges();

    setTimeout(() => {
      try {
        const contaDestino = this.accountService.getAccountByNum(this.transferencia.contaDestino);

        if (!contaDestino) {
          this.toastr.error('A conta informada não existe', 'Erro');
          return;
        }

        const valor = + this.transferencia.valor;
        const transacao = new Transacao(new Date(), TipoMovimentacao.TRANSFERENCIA, this.clienteOrigem, contaDestino.cliente, valor);
        const result = this.transactionService.registerNewTransaction(transacao);

        if (result.success) {
          this.toastr.success(result.message, 'Sucesso');
          console.log("Transação foi cadastrada");
          this.router.navigate(['cliente/', this.user?.id])
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
