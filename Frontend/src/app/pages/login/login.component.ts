import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { User } from '../../shared/models/user.model';
import { UserService } from '../../services/auth/user.service';
import { Router } from '@angular/router';
import { TipoUsuario } from '../../shared/enums/TipoUsuario';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  @ViewChild('meuForm') meuForm!: NgForm;
  login: User;
  loginError: string | null = null;

  constructor(private readonly userService: UserService, private readonly router: Router) {
    this.login = new User();
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

    const temp = this.userService.getUserByLoginAndPassword(this.login.login, this.login.senha);

    if(temp){
      this.login = temp;
      this.loginError = null;

      if(this.login.tipoUsuario === TipoUsuario.CLIENTE){
        const user = this.login;
        localStorage.setItem('usuarioLogado', JSON.stringify(user));
        this.router.navigate(['/cliente/', user.usuario?.id])
      }
      else if(this.login.tipoUsuario === TipoUsuario.GERENTE){
        const user = this.login;
        localStorage.setItem('usuarioLogado', JSON.stringify(user));
        this.router.navigate(['/gerente/', user.usuario?.id]);
      }
      else{
        //Tela Admin
      }
    }
    else{
      const loginControl = this.meuForm.controls['password'];
      if (loginControl) {
        loginControl.setErrors({ 'incorrect': true });
      }
    }
  }

}
