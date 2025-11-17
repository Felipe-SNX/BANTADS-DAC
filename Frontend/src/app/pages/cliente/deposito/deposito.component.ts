import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective } from 'ngx-mask';
import { UserService } from '../../../services/user/user.service';
import { ContaService } from '../../../services/conta/conta.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { ContaDepositoRequest } from '../../../shared/models/conta-deposito-request.model';

@Component({
  selector: 'app-deposito',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxMaskDirective, SidebarComponent, LoadingComponent],
  templateUrl: './deposito.component.html',
  styleUrl: './deposito.component.css'
})
export class DepositoComponent implements OnInit{
  @ViewChild('depositoForm') depositoForm!: NgForm;

  public loadingBotao: boolean = false;
  public loadingPagina: boolean = false;
  
  public valor: string = ''; 
  private numconta: string = ''; 

  private readonly toastr = inject(ToastrService);

  constructor(
    private readonly accountService: ContaService,
    private readonly router: Router,
    private readonly userService: UserService,
    private readonly customerService: ClienteService
  ) {}

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
        this.numconta = cliente.conta;
        
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
    this.depositoForm.form.markAllAsTouched();

    if (this.depositoForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    this.loadingBotao = true;

    try {
      const valorNumerico = +this.valor;

      if (Number.isNaN(valorNumerico) || valorNumerico <= 0) {
        this.toastr.error('Valor de depósito inválido.', 'Erro');
        throw new Error('Valor inválido');
      }

      const contadepositorequest = new ContaDepositoRequest(valorNumerico);
      await this.accountService.depositarConta(this.numconta, contadepositorequest);
      
      this.toastr.success('Valor depositado com sucesso', 'Sucesso');
      this.router.navigate(['/cliente']);

    } catch (error: any) {
      console.error(error);
      if (error.message !== 'Valor inválido') {
        this.toastr.error('Por favor, tente novamente', 'Erro');
      }
    } finally {
      this.loadingBotao = false;
    }
  }

}