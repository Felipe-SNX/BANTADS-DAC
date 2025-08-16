import { Component, inject, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { Cliente } from '../../shared/models/cliente.model';
import { CommonModule } from '@angular/common';
import { CpfValidatorDirective } from '../../shared/directives/cpf-validator.directive';
import { InputGreaterThanZeroDirective } from '../../shared/directives/input-greater-than-zero.directive';
import { AutocadastroService, SaveResult } from '../../services/autocadastro.service';
import { GerenteService } from '../../services/gerente.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-autocadastro',
  standalone: true,
  imports: [NgxMaskDirective, FormsModule, CommonModule, CpfValidatorDirective, InputGreaterThanZeroDirective],
  templateUrl: './autocadastro.component.html',
  styleUrl: './autocadastro.component.css'
})
export class AutocadastroComponent {
  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  cliente: Cliente;

  constructor(private readonly clientService: AutocadastroService, private readonly managerService: GerenteService) {
    this.cliente = new Cliente();
  }

  onSubmit() {
    //Marca todas as caixas como touched para aparecer os erros caso existam
    Object.values(this.meuForm.controls).forEach(control => {
      control.markAsTouched();
    });

    //Se tiver erros não prossegue
    if (this.meuForm.invalid) {
      console.log("Formulário inválido. Por favor, corrija os erros.");
      return;
    }

    const result: SaveResult = this.clientService.saveClient(this.cliente);

    if(result.success){
      this.managerService.addCustomerToManager(this.cliente);
      this.toastr.success('A solicitação foi enviada com sucesso!', 'Sucesso');
    }else{
      console.log(result.message);
      this.toastr.warning('Já existe um cliente com CPF informado!', 'Erro');
    }

    console.log(this.cliente);
  }
}
