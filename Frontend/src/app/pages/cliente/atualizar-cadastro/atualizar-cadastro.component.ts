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

      //Transforma os campos no objeto cliente

      try{
        const updateCustomer = new PerfilInfo();
        updateCustomer.nome = this.cliente.dadosPessoais.nome;
        updateCustomer.email = this.cliente.dadosPessoais.email;
        updateCustomer.salario = this.cliente.dadosPessoais.salario;
        updateCustomer.telefone = this.cliente.dadosPessoais.telefone;
        updateCustomer.endereco =  this.cliente.endereco.complemento + ", " + this.cliente.endereco.tipo + ", " + this.cliente.endereco.logradouro + ", " + this.cliente.endereco.numero;
        updateCustomer.cep = this.cliente.endereco.cep;
        updateCustomer.cidade = this.cliente.endereco.cidade;
        updateCustomer.estado = this.cliente.endereco.estado;

        console.log(updateCustomer);

        await this.customerService.atualizarCliente(updateCustomer, this.cliente.dadosPessoais.cpf);
        this.toastr.success('Cliente atualizado com sucesso!', 'Sucesso');
        this.router.navigate(['/cliente']);
      } catch(error) {
        this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
      }

    }
}
