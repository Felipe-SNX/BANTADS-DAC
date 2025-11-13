import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { User } from '../../shared/models/user.model';
import { UserService } from '../../services/user/user.service';
import { Router } from '@angular/router';
import { TipoUsuario } from '../../shared/enums/TipoUsuario';
import { LoginRequest } from "../../shared/models/loginRequest.model";
import { LoginResponse } from "../../shared/models/loginResponse.model";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  @ViewChild('meuForm') meuForm!: NgForm;
  login: LoginRequest;
  loginError: string | null = null;
  isLoading = false;

  constructor(
    private readonly userService: UserService,
    private readonly router: Router
  ) {
    this.login = new LoginRequest();
  }

  async onSubmit() {
    Object.values(this.meuForm.controls).forEach(control => {
      control.markAsTouched();
    });

    if (this.meuForm.invalid) {
      console.log("Formulário inválido.");
      return;
    }

    this.isLoading = true;
    this.loginError = null;

    try {
      const respostaLogin = await this.userService.login(
        new LoginRequest(this.login.login, this.login.senha)
      );

      if (!respostaLogin || !respostaLogin.access_token) {
        throw new Error("Resposta inválida do servidor.");
      }

      this.handleLoginSuccess(respostaLogin);

    } catch (error: any) {
      console.error("Erro durante o login:", error);
      this.handleLoginError(error);

    } finally {
      this.isLoading = false;
    }
  }

  private handleLoginSuccess(resposta: LoginResponse) {
    const token = resposta.access_token.replace(/"/g, '');
    const cpf = resposta.usuario.cpf.replace(/"/g, '');
    sessionStorage.setItem('cpf', cpf);
    sessionStorage.setItem('token', token);

    switch (resposta.tipo) {
      case TipoUsuario.CLIENTE:
        this.router.navigate(['/cliente']);
        break;
      case TipoUsuario.GERENTE:
        this.router.navigate(['/gerente']);
        break;
      case TipoUsuario.ADMINISTRADOR:
        this.router.navigate(['/admin']);
        break;
      default:
        this.loginError = "Tipo de usuário desconhecido.";
        sessionStorage.clear();
    }
  }

  private handleLoginError(error: any) {
    if (error.response && error.response.status === 401) {
      this.loginError = "Login ou senha incorretos.";
    } else {
      this.loginError = "Erro inesperado. Tente novamente mais tarde.";
    }

    const passwordControl = this.meuForm.controls['password'];
    if (passwordControl) {
      passwordControl.setErrors({ 'incorrect': true });
    }
  }
}
