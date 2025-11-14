import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormsModule, NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Cliente } from '../../../shared/models/cliente.model';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user/user.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { EnderecoFormComponent } from '../autocadastro/formularios/endereco-form/endereco-form.component';
import { PessoaFormComponent } from '../autocadastro/formularios/pessoa-form/pessoa-form.component';
import { LocalStorageResult } from '../../../shared/utils/LocalStorageResult';
import { DadoCliente } from '../../../shared/models/dados-cliente.model';

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
    private readonly userService: UserService,
    private readonly router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    const cpf = this.userService.getCpfUsuario();
    if(cpf === '') this.router.navigate(['/']);
    const customer = await this.customerService.getCliente(cpf);

    if(!customer){
      this.router.navigate(['/']);
    }

    this.loadClienteData(customer);
  }

  loadClienteData(customer: DadoCliente): void{
    const dadosEndereco = customer.endereco.split(',');

    const clienteTransformado = {
      dadosPessoais: {
        nome: customer.nome,
        cpf: customer.cpf,
        email: customer.email,
        telefone: customer.telefone,
        salario: customer.salario
      },

      endereco: {
        tipo: dadosEndereco[1] || '',
        logradouro: dadosEndereco[2] || '',
        numero: Number.parseInt(dadosEndereco[3]) || 0,
        complemento: dadosEndereco[0] || '',
        cep: customer.cep,
        cidade: customer.cidade,
        estado: customer.estado
      }
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

      /*const updateCustomer = new Cliente(
        this.cliente.dadosPessoais.nome,
        this.cliente.dadosPessoais.email,
        this.cliente.dadosPessoais.cpf,
        this.cliente.endereco,
        this.cliente.dadosPessoais.telefone,
        this.cliente.dadosPessoais.salario
      );

      const result: LocalStorageResult = this.customerService.updateClient(updateCustomer);

      if(result.success){
        this.toastr.success('Cliente atualizado com sucesso!', 'Sucesso');
        this.router.navigate(['/cliente']);
      }else{
        console.log(result.message);
        this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
      }

      console.log(updateCustomer);*/
    }
}
