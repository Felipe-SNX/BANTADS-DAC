import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormsModule, NgForm } from '@angular/forms';
import { EnderecoFormComponent } from '../autocadastro/formularios/endereco-form/endereco-form.component';
import { PessoaFormComponent } from '../autocadastro/formularios/pessoa-form/pessoa-form.component';
import { ToastrService } from 'ngx-toastr';
import { Cliente } from '../../shared/models/cliente.model';
import { ClienteService, SaveResult } from '../../services/cliente/cliente.service';
import { GerenteService } from '../../services/gerente/gerente.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-atualizar-cadastro',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    EnderecoFormComponent,
    PessoaFormComponent
  ],
  templateUrl: './atualizar-cadastro.component.html',
  styleUrl: './atualizar-cadastro.component.css'
})
export class AtualizarCadastroComponent implements OnInit{

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
      private readonly managerService: GerenteService,
      private readonly route: ActivatedRoute
    ) {}

  ngOnInit(): void {
    this.loadClienteData();
  }

  loadClienteData(): void{

    const idDoClienteParaEditar = +this.route.snapshot.paramMap.get('id')!;

    if (!idDoClienteParaEditar) {
      this.toastr.error('ID do cliente não encontrado na URL.', 'Erro de Rota');
      return;
    }

    const arrayDeClientesString = localStorage.getItem('clientes');
    
    if (!arrayDeClientesString) {
        this.toastr.error('A lista de clientes não foi encontrada no localStorage.', 'Erro');
        return;
    }

    const todosOsClientes = JSON.parse(arrayDeClientesString);

        const clienteEncontrado = todosOsClientes.find((c: { id: number; }) => c.id === idDoClienteParaEditar);

    if (!clienteEncontrado) {
        this.toastr.error(`O cliente com o ID ${idDoClienteParaEditar} não foi encontrado na lista.`, 'Erro');
        return;
    }

    const clienteTransformado = {
      dadosPessoais: {
        nome: clienteEncontrado.nome,
        cpf: clienteEncontrado.cpf,
        email: clienteEncontrado.email,
        telefone: clienteEncontrado.telefone,
        salario: clienteEncontrado.salario
      },
  
      endereco: clienteEncontrado.endereco 
    };

    this.cliente = clienteTransformado;

    console.log('Cliente encontrado e transformado para o formulário:', this.cliente);
  }

 
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
      this.cliente.dadosPessoais = formValue.dadosPessoais;
      this.cliente.endereco = formValue.endereco;
      
      const updateCustomer = new Cliente(
        0,
        formValue.dadosPessoais.name,
        formValue.dadosPessoais.email,
        formValue.dadosPessoais.CPF,
        formValue.endereco,
        formValue.dadosPessoais.telefone,
        formValue.dadosPessoais.salario);
  
      const result: SaveResult = this.customerService.saveClient(updateCustomer);
  
      if(result.success){
        this.managerService.addCustomerToManager(updateCustomer);
        this.toastr.success('A solicitação foi enviada com sucesso!', 'Sucesso');
        localStorage.setItem('clientes', JSON.stringify(this.cliente));
      }else{
        console.log(result.message);
        this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
      }
  
      console.log(updateCustomer);
    }
}
