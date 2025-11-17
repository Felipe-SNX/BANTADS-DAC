import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { NgxMaskDirective } from 'ngx-mask';
import { UserService } from '../../../services/user/user.service';
import { ContaService } from '../../../services/conta/conta.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { DadoCliente } from '../../../shared/models/dados-cliente.model';
import { ContaDepositoRequest } from '../../../shared/models/conta-deposito-request.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-saque',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskDirective, LoadingComponent],
  templateUrl: './saque.component.html',
  styleUrl: './saque.component.css'
})
export class SaqueComponent implements OnInit{
  @ViewChild('saqueForm') saqueForm!: NgForm;

  public loadingBotao: boolean = false;
  public loadingPagina: boolean = false;
  public saldoVisivel: boolean = false;
  
  public valorSaque: string = ''; 
  public saldo: number = 0;
  public limite: number = 0;
  public customer: DadoCliente = new DadoCliente(); 
  
  private numConta: string = ''; 
  
  private readonly toastr = inject(ToastrService);

  constructor(
    private readonly accountService: ContaService,
    private readonly router: Router,
    private readonly userService: UserService,
    private readonly customerService: ClienteService
  ) {}

  public toggleVisibilidadeSaldo(): void {
    this.saldoVisivel = !this.saldoVisivel;
  }

  async ngOnInit(): Promise<void> {
    this.loadingPagina = true;
    try {
      const user = this.userService.isLogged(); 
      if (user) { 
        const cpf = this.userService.getCpfUsuario();
        const cliente = await this.customerService.getCliente(cpf);
        
        if (!cliente || !cliente.conta) {
          throw new Error('Dados do cliente ou número da conta não encontrados.');
        }
        
        this.customer = cliente;
        this.numConta = cliente.conta;  
        this.saldo = cliente.saldo;
        this.limite = cliente.limite;
        
      } else {
        this.toastr.error('Sessão expirada. Por favor, faça o login novamente.', 'Erro');
        this.router.navigate(['/']);
      }
    } catch (error) {
      console.error('Falha ao carregar dados do cliente:', error);
      this.toastr.error('Não foi possível carregar os dados da sua conta.', 'Erro');
      this.router.navigate(['/cliente']); // Volta para a home do cliente
    } finally {
      this.loadingPagina = false;
    }
  }

  async onSubmit() {
    this.saqueForm.form.markAllAsTouched();

    if (this.saqueForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    this.loadingBotao = true;

    try {
      const valorNumerico = +this.valorSaque;
      
      if (Number.isNaN(valorNumerico) || valorNumerico <= 0) {
        this.toastr.error('Valor de saque inválido.', 'Erro');
        throw new Error('Valor inválido'); 
      }
      
      if (valorNumerico > (this.saldo + this.limite)) {
         this.toastr.error('Saldo insuficiente (incluindo limite).', 'Erro');
         throw new Error('Saldo insuficiente');
      }

      const contaDepositoRequest = new ContaDepositoRequest(valorNumerico);
      await this.accountService.sacarConta(this.numConta, contaDepositoRequest);
          
      this.toastr.success("Saque efetuado com sucesso", 'Sucesso');
      this.router.navigate(['/cliente']);
    
    } catch (error: any) {
      console.error(error);
      if (error.message !== 'Valor inválido' && error.message !== 'Saldo insuficiente') {
        this.toastr.error('Erro ao processar o saque. Tente novamente.', 'Erro');
      }
    } finally {
      this.loadingBotao = false;
    }
  }
  
}