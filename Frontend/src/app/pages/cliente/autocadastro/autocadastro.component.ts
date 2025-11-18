import { Component, inject, ViewChild } from '@angular/core';
import { FormGroup, FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { EnderecoFormComponent } from './formularios/endereco-form/endereco-form.component';
import { PessoaFormComponent } from "./formularios/pessoa-form/pessoa-form.component";
import { Router } from '@angular/router';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { AutocadastroModel } from '../../../shared/models/autocadastro.model';

@Component({
  selector: 'app-autocadastro',
  standalone: true,
  imports: [
    FormsModule, 
    CommonModule,
    EnderecoFormComponent,
    PessoaFormComponent
  ],
  templateUrl: './autocadastro.component.html',
  styleUrl: './autocadastro.component.css'
})
export class AutocadastroComponent {
  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  public cliente = {
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
    private readonly router: Router
  ) {
  }

  avancarEtapa() {
    this.etapaAtual++;
  }

  voltarEtapa() { this.etapaAtual--; }

  async onSubmit() {

    this.meuForm.control.markAllAsTouched();

    if (this.meuForm.invalid) {
      console.log("Formulário inválido (Pai). Por favor, corrija os erros.");
      this.toastr.warning("Formulário inválido. Verifique os campos.", "Erro");
      return;
    }

    try {
      const customer: AutocadastroModel = new AutocadastroModel();
      
      customer.cpf = this.cliente.dadosPessoais.cpf;
      customer.email = this.cliente.dadosPessoais.email;
      customer.nome = this.cliente.dadosPessoais.nome;
      customer.salario = this.cliente.dadosPessoais.salario;
      customer.telefone = this.cliente.dadosPessoais.telefone;

      customer.cidade = this.cliente.endereco.cidade;
      customer.estado = this.cliente.endereco.estado;
      customer.cep = this.cliente.endereco.cep;

      customer.endereco = [
        this.cliente.endereco.complemento || '',
        this.cliente.endereco.tipo || '',
        this.cliente.endereco.logradouro || '',
        this.cliente.endereco.numero || 0
      ].join(', ');

      await this.customerService.cadastrarCliente(customer);
      this.toastr.success('A solicitação foi enviada com sucesso!', 'Sucesso');
      this.router.navigate(['/']);

    } catch (error: any) {
      if (error.status === 409) {
        this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
      } else {
        this.toastr.warning('Não foi possível cadastrar um cliente!', 'Erro');
      }
      console.log(error);
    }
  }
}