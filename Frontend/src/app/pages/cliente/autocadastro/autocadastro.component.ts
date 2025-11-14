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
export class AutocadastroComponent{
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

  avancarEtapa() { this.etapaAtual++; }
  voltarEtapa() { this.etapaAtual--; }

  async onSubmit() {
    //Marca todas as caixas como touched para aparecer os erros caso existam
    Object.values(this.meuForm.controls).forEach(control => {
      if (control instanceof FormGroup) {
        Object.values(control.controls).forEach(innerControl => {
          innerControl.markAsTouched();
        });
      } else {
        control.markAsTouched();
      }
    });

    //Se tiver erros não prossegue
    if (this.meuForm.invalid) {
      console.log("Formulário inválido. Por favor, corrija os erros.");
      return;
    }

    try{
      //Transforma os campos no objeto cliente
      const formValue = this.meuForm.value;
      const customer : AutocadastroModel = new AutocadastroModel();
      customer.cpf = formValue.dadosPessoais.CPF;
      customer.email = formValue.dadosPessoais.email;
      customer.nome = formValue.dadosPessoais.name;
      customer.salario = formValue.dadosPessoais.salario;
      customer.telefone = formValue.dadosPessoais.telefone;
      customer.cidade = formValue.endereco.cidade;
      customer.estado = formValue.endereco.estado;
      customer.cep = formValue.endereco.cep;
      customer.endereco = formValue.endereco.complemento + ", " + formValue.endereco.tipo + ", " + formValue.endereco.logradouro + ", " + formValue.endereco.numero;

      await this.customerService.cadastrarCliente(customer);
      this.toastr.success('A solicitação foi enviada com sucesso!', 'Sucesso');
      this.router.navigate(['/']);

    } catch (error: any){
      if(error.status === 409){
        this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
      } else{
        this.toastr.warning('Não foi possível cadastrar um cliente!', 'Erro');
      }
      console.log(error);
    }
  }
}
