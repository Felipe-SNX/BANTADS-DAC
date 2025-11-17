import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { UserService } from '../../../services/user/user.service';
import { ContaService } from '../../../services/conta/conta.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { DadoCliente } from '../../../shared/models/dados-cliente.model';
import { ContaTransferenciaRequest } from '../../../shared/models/conta-transferencia-request.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxMaskDirective, SidebarComponent, LoadingComponent],
  templateUrl: './transferencia.component.html',
  styleUrl: './transferencia.component.css'
})
export class TransferenciaComponent implements OnInit{
  @ViewChild('transferForm') transferForm!: NgForm;
  
  public loadingBotao: boolean = false;
  public loadingPagina: boolean = false;
  private readonly toastr = inject(ToastrService);

  public saldo: number = 0;
  public limite: number = 0;
  public saldoVisivel: boolean = false;
  private contaOrigem: string = '';
  public clienteOrigem: DadoCliente = new DadoCliente();

  public transferencia = {
    contaDestino: "",
    valor: ""
  }

  constructor(
    private readonly accountService: ContaService,
    private readonly router: Router,
    private readonly userService: UserService,
    private readonly customerService: ClienteService
  ){ }

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
        
        this.clienteOrigem = cliente;
        this.contaOrigem = cliente.conta;  
        this.saldo = cliente.saldo; 
        this.limite = cliente.limite;      
      } else {
        this.toastr.error('Sessão expirada. Por favor, faça o login novamente.', 'Erro');
        this.router.navigate(['/']);
      }
    } catch (error) {
      console.error('Falha ao carregar dados do cliente:', error);
      this.toastr.error('Não foi possível carregar os dados da sua conta.', 'Erro');
      this.router.navigate(['/cliente']);
    } finally {
      this.loadingPagina = false;
    }
  }

  toggleVisibilidadeSaldo(): void {
    this.saldoVisivel = !this.saldoVisivel;
  }

  async onSubmit() {
    this.transferForm.form.markAllAsTouched();

    if (this.transferForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    this.loadingBotao = true;
    
    try {
      const valor = +this.transferencia.valor;
          
      if (Number.isNaN(valor) || valor <= 0) {
        this.toastr.error('Valor de transferência inválido.', 'Erro');
        throw new Error('Valor inválido'); 
      }
          
      if (valor > (this.saldo + this.limite)) {
        this.toastr.error('Saldo insuficiente (incluindo limite).', 'Erro');
        throw new Error('Saldo insuficiente');
      }
      
      if (this.transferencia.contaDestino === this.contaOrigem) {
        this.toastr.error('Não é possível transferir para a própria conta.', 'Erro');
        throw new Error('Conta de destino inválida');
      }
    
      const contaTransferenciaRequest = new ContaTransferenciaRequest(valor, this.transferencia.contaDestino);
      await this.accountService.transferirEntreContas(this.contaOrigem, contaTransferenciaRequest);
                  
      this.toastr.success("Transferência efetuado com sucesso", 'Sucesso');
      this.router.navigate(['/cliente']);
        
    } catch (error: any) {
      console.error(error);
      
      const errosIgnorados = ['Valor inválido', 'Saldo insuficiente', 'Conta de destino inválida'];
      if (!errosIgnorados.includes(error.message)) {
        this.toastr.error('Erro ao processar a transferência. Tente novamente.', 'Erro');
      }
    } finally {
      this.loadingBotao = false;
    }
  }

}