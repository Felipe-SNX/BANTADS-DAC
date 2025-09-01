import { Component, ViewChild } from '@angular/core';
import { Autenticacao } from '../../shared/models/autenticacao.model';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  @ViewChild('meuForm') meuForm!: NgForm;
  login: Autenticacao;

  constructor() {
    this.login = new Autenticacao();
  }

  onSubmit(){
    Object.values(this.meuForm.controls).forEach(control => {
      control.markAsTouched();
    });

    //Se tiver erros não prossegue
    if (this.meuForm.invalid) {
      console.log("Formulário inválido. Por favor, corrija os erros.");
      return;
    }

    console.log(this.login);
  }

}
