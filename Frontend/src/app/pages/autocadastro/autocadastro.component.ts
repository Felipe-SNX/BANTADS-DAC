import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { Cliente } from '../../shared/models/cliente.model';
import { CommonModule } from '@angular/common';
import { CpfValidatorDirective } from '../../shared/directives/cpf-validator.directive';

@Component({
  selector: 'app-autocadastro',
  standalone: true,
  imports: [NgxMaskDirective, FormsModule, CommonModule, CpfValidatorDirective],
  templateUrl: './autocadastro.component.html',
  styleUrl: './autocadastro.component.css'
})
export class AutocadastroComponent {
  cliente: Cliente;

  constructor() {
    this.cliente = new Cliente();
  }

  onSubmit() {
    console.log(this.cliente);
  }
}
