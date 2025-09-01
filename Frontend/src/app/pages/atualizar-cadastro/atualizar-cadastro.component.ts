import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormsModule, NgForm } from '@angular/forms';
import { EnderecoFormComponent } from '../autocadastro/formularios/endereco-form/endereco-form.component';
import { PessoaFormComponent } from '../autocadastro/formularios/pessoa-form/pessoa-form.component';
import { AutocadastroComponent } from '../autocadastro';
import { ToastrService } from 'ngx-toastr';
import { Cliente } from '../../shared/models/cliente.model';
import { ClienteService, SaveResult } from '../../services/cliente/cliente.service';
import { GerenteService } from '../../services/gerente/gerente.service';
import { MockDataService } from '../../services/mock/mock-data.service';

@Component({
  selector: 'app-atualizar-cadastro',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    EnderecoFormComponent,
    PessoaFormComponent,
    AutocadastroComponent,
  ],
  templateUrl: './atualizar-cadastro.component.html',
  styleUrl: './atualizar-cadastro.component.css'
})
export class AtualizarCadastroComponent {

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
      private readonly managerService: GerenteService
    ) {}

 
    avancarEtapa() { this.etapaAtual++; }
    voltarEtapa() { this.etapaAtual--; }
  
    onSubmit() {
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
  
      //Transforma os campos no objeto cliente
      const formValue = this.meuForm.value;
      const customer = new Cliente(
        0,
        formValue.dadosPessoais.name,
        formValue.dadosPessoais.email,
        formValue.dadosPessoais.CPF,
        formValue.endereco,
        formValue.dadosPessoais.telefone,
        formValue.dadosPessoais.salario);
  
      const result: SaveResult = this.customerService.saveClient(customer);
  
      if(result.success){
        this.managerService.addCustomerToManager(customer);
        this.toastr.success('A solicitação foi enviada com sucesso!', 'Sucesso');
        localStorage.setItem('cliente', JSON.stringify(this.cliente));
      }else{
        console.log(result.message);
        this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
      }
  
      console.log(customer);
    }

    buscarCliente() {
      const dados = localStorage.getItem('meuItem');

      if (dados) {
      const objeto = JSON.parse(dados);
      console.log(objeto);
      } else {
        console.log('Nenhum dado encontrado');
      }
    }

}
