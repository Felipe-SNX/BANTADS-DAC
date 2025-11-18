import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormsModule, NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { EnderecoFormComponent } from '../autocadastro/formularios/endereco-form/endereco-form.component';
import { PessoaFormComponent } from '../autocadastro/formularios/pessoa-form/pessoa-form.component';
import { DadoCliente } from '../../../shared/models/dados-cliente.model';
import { PerfilInfo } from '../../../shared/models/perfil-info.model';

@Component({
  selector: 'app-atualizar-cadastro',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    EnderecoFormComponent,
    PessoaFormComponent,
    SidebarComponent
  ],
  templateUrl: './atualizar-cadastro.component.html',
  styleUrl: './atualizar-cadastro.component.css'
})
export class AtualizarCadastroComponent implements OnInit{

  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  public cliente: any = { 
    dadosPessoais: {
      nome: '',
      cpf: '',
      email: '',
      telefone: '',
      salario: 0,
    },
    endereco: {
      tipo: '',
      logradouro: '',
      numero: 0,
      complemento: '',
      cep: '',
      cidade: '',
      estado: ''
    }
  };

  public etapaAtual: number = 1;

  constructor(
    private readonly customerService: ClienteService,
    private readonly userService: UserService,
    private readonly router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    const cpf = this.userService.getCpfUsuario();
    if(cpf === '') {
      this.router.navigate(['/']);
      return; 
    }

    const customer = await this.customerService.getCliente(cpf);

    if(!customer){
      this.toastr.warning('Cliente não encontrado.', 'Erro');
      this.router.navigate(['/']);
      return; 
    }

    this.loadClienteData(customer);
  }

  loadClienteData(customer: DadoCliente): void {
    const parts = (customer.endereco || '').split(',').map(p => p.trim());
    const len = parts.length;

    const enderecoObj = {
      tipo: '',
      logradouro: '',
      numero: 0,
      complemento: '',
      cep: customer.cep,
      cidade: customer.cidade,
      estado: customer.estado
    };

    if (len >= 4) {
      enderecoObj.complemento = parts[0];
      enderecoObj.tipo = parts[1];
      enderecoObj.logradouro = parts[2];
      enderecoObj.numero = this.parseNumeroSeguro(parts[3]);
    } 
    else if (len === 3) {
      enderecoObj.tipo = parts[0];
      enderecoObj.logradouro = parts[1];
      enderecoObj.numero = this.parseNumeroSeguro(parts[2]);
    } 
    else if (len === 2) {
      enderecoObj.logradouro = parts[0];
      enderecoObj.numero = this.parseNumeroSeguro(parts[1]);
    } 
    else if (len === 1) {
      enderecoObj.logradouro = parts[0];
    }

    const clienteTransformado = {
      dadosPessoais: {
        nome: customer.nome,
        cpf: customer.cpf,
        email: customer.email,
        telefone: customer.telefone,
        salario: customer.salario
      },
      endereco: enderecoObj
    };

    this.cliente = clienteTransformado;
    console.log('Endereço processado com segurança:', this.cliente.endereco);
  }

  private parseNumeroSeguro(valor: string): number {
    const num = Number.parseInt(valor);
    return Number.isNaN(num) ? 0 : num;
  }

    avancarEtapa() { this.etapaAtual++; }
    voltarEtapa() { this.etapaAtual--; }

    async onSubmit() {
      Object.values(this.meuForm.controls).forEach(control => {
        if (control instanceof FormGroup) {
          Object.values(control.controls).forEach(innerControl => {
            innerControl.markAsTouched();
          });
        } else {
          control.markAsTouched();
        }
      });

      if (this.meuForm.invalid) {
        console.log("Formulário inválido. Por favor, corrija os erros.");
        this.toastr.warning('Formulário inválido. Verifique os campos.', 'Erro');
        return;
      }

      try{
        const updateCustomer = new PerfilInfo();
        updateCustomer.nome = this.cliente.dadosPessoais.nome;
        updateCustomer.email = this.cliente.dadosPessoais.email;
        updateCustomer.salario = this.cliente.dadosPessoais.salario;
        updateCustomer.telefone = this.cliente.dadosPessoais.telefone;
        updateCustomer.endereco = [
          this.cliente.endereco.complemento || '',
          this.cliente.endereco.tipo || '',
          this.cliente.endereco.logradouro || '',
          this.cliente.endereco.numero || 0
        ].join(', ');
        updateCustomer.cep = this.cliente.endereco.cep;
        updateCustomer.cidade = this.cliente.endereco.cidade;
        updateCustomer.estado = this.cliente.endereco.estado;

        console.log("Enviando atualização:", updateCustomer);

        await this.customerService.atualizarCliente(updateCustomer, this.cliente.dadosPessoais.cpf);
        this.toastr.success('Cliente atualizado com sucesso!', 'Sucesso');
        this.router.navigate(['/cliente']);
      } catch(error) {
        console.error("Erro ao atualizar cliente:", error);
        this.toastr.error('Falha ao atualizar o cadastro. Tente novamente.', 'Erro');
      }
    }
}