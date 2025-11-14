import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { ActivatedRoute } from '@angular/router';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DadoCliente } from '../../../shared/models/dados-cliente.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { NgxMaskPipe } from 'ngx-mask';

@Component({
  selector: 'app-consulta-cliente',
  templateUrl: './consulta-cliente.component.html',
  styleUrls: ['./consulta-cliente.component.css'],
  imports: [SidebarComponent, CommonModule, FormsModule, LoadingComponent, NgxMaskPipe],
  standalone: true
})
export class ConsultaClienteComponent implements OnInit {
  
  public cliente: DadoCliente | null = null;
  public cpfInput: string = '';
  public erroMensagem: string = '';
  public loading: boolean = false;

  constructor(
    private readonly clienteService: ClienteService,
    private readonly route: ActivatedRoute,
  ) { }

  async ngOnInit() {
    const cpfDaRota = this.route.snapshot.paramMap.get('cpf');
    
    if (cpfDaRota) {
      this.cpfInput = cpfDaRota; 
      await this.buscarCliente(cpfDaRota); 
    }
  }

  public get saldoNegativo(): boolean {
    return this.cliente ? this.cliente.saldo < 0 : false;
  }

  public async onConsultarClick(): Promise<void> {
    await this.buscarCliente(this.cpfInput);
  }

  private async buscarCliente(cpf: string): Promise<void> {
    if (!cpf) {
      this.erroMensagem = 'Por favor, digite um CPF.';
      return;
    }

    this.loading = true;
    this.cliente = null;
    this.erroMensagem = '';

    try {
      const cpfLimpo = cpf.replaceAll(/\D/g, '');
      const clienteEncontrado = await this.clienteService.getCliente(cpfLimpo);

      if (clienteEncontrado) {
        this.cliente = clienteEncontrado;
      } else {
        this.erroMensagem = 'Cliente não encontrado. Verifique o CPF e tente novamente.';
      }
    } catch (error) {
      console.error('Erro ao buscar cliente:', error);
      this.erroMensagem = 'Ocorreu um erro no servidor. Tente novamente mais tarde.';
    } finally {
      this.loading = false;
    }
  }
}