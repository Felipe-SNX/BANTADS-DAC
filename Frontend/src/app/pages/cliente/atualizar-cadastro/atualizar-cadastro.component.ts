import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormsModule, NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Cliente } from '../../../shared/models/cliente.model';
import { ClienteService, SaveResult } from '../../../services/cliente/cliente.service';
import { Router } from '@angular/router';
import { UserService } from '../../../services/auth/user.service';
import { User } from '../../../shared/models/user.model';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { EnderecoFormComponent } from '../autocadastro/formularios/endereco-form/endereco-form.component';
import { PessoaFormComponent } from '../autocadastro/formularios/pessoa-form/pessoa-form.component';

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
  user: User | null | undefined;
  
  constructor(
    private readonly customerService: ClienteService,
    private readonly userService: UserService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    const temp = this.userService.findLoggedUser();

    if(!temp) this.router.navigate(['/']);

    this.user = temp;

    this.loadClienteData(this.user?.usuario as Cliente);
  }

  loadClienteData(customer: Cliente): void{
    const clienteTransformado = {
      dadosPessoais: {
        nome: customer.nome,
        cpf: customer.cpf,
        email: customer.email,
        telefone: customer.telefone,
        salario: customer.salario
      },
  
      endereco: {
        tipo: customer.endereco.tipo,
        logradouro: customer.endereco.logradouro,
        numero: customer.endereco.numero,
        complemento: customer.endereco.complemento,
        cep: customer.endereco.cep,
        cidade: customer.endereco.cidade,
        estado: customer.endereco.estado
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
      const formValue = this.meuForm.value;
      this.cliente.dadosPessoais = formValue.dadosPessoais;
      this.cliente.endereco = formValue.endereco;
      
      const updateCustomer = new Cliente(
        this.user?.usuario?.id,
        formValue.dadosPessoais.name,
        formValue.dadosPessoais.email,
        formValue.dadosPessoais.CPF,
        formValue.endereco,
        formValue.dadosPessoais.telefone,
        formValue.dadosPessoais.salario
      );
  
      const result: SaveResult = this.customerService.updateClient(updateCustomer);
  
      if(result.success){
        this.userService.updateUserCustomerData(this.user, updateCustomer);
        this.userService.updateLoggedUser(this.user);
      }else{
        console.log(result.message);
        this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
      }
  
      console.log(updateCustomer);
    }
}
