import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
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

@Component({
  selector: 'app-saque',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskDirective],
  templateUrl: './saque.component.html',
  styleUrl: './saque.component.css'
})
export class SaqueComponent implements OnInit{
  @ViewChild('saqueForm') saqueForm!: NgForm; 
  user: User | null | undefined;
  loading: boolean = false;
  private readonly toastr = inject(ToastrService);
    
  public saldo: number = 0; 
  public limite: number = 0;
  public saldoVisivel: boolean = false;
  conta: Conta | undefined;
  public valorSaque: string = '';
  customer: Cliente | undefined;

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

  toggleVisibilidadeSaldo(): void {
    this.saldoVisivel = !this.saldoVisivel;
  }

  ngOnInit(): void {
    const temp = this.userService.findLoggedUser();

    if(!temp) this.router.navigate(['/']);

    this.user = temp; 
    const tempCustomer = this.customerService.getClientById(this.user?.idPerfil as number);
    const tempAccount = this.accountService.getAccountByCustomer(tempCustomer as Cliente);
  
    if(!tempAccount){
      this.router.navigate(['/']);
    }
    else{
      this.customer = tempCustomer;
      this.conta = tempAccount;
      this.saldo = this.conta.saldo;
      this.limite = this.conta.limite;
    }
  }

  onSubmit() {
    Object.values(this.saqueForm.controls).forEach(control => {
      control.markAsTouched();
    });
    
    if (this.saqueForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }
    
    this.loading = true;
    this.cd.detectChanges();
    
    setTimeout(() => {
      try {
      
        const valor = +this.valorSaque;
        const transacao = new Transacao(new Date(), TipoMovimentacao.SAQUE, this.customer as Cliente, null, valor);
        const result = this.transactionService.registerNewTransaction(transacao);
            
        if (result.success) {
          this.toastr.success("Saque efetuado com sucesso", 'Sucesso');
          console.log("Saque bem sucedido");
          this.router.navigate(['cliente/', this.user?.idPerfil])
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
