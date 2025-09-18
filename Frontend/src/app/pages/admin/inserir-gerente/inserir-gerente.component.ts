import { CommonModule } from "@angular/common";
import { Component, ViewChild, inject } from "@angular/core";
import { FormsModule, NgForm } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { NgxMaskDirective } from "ngx-mask";
import { ToastrService } from "ngx-toastr";
import { UserService } from "../../../services/auth/user.service";
import { GerenteService } from "../../../services/gerente/gerente.service";
import { SidebarComponent } from "../../../shared/components/sidebar/sidebar.component";
import { TipoUsuario } from "../../../shared/enums/TipoUsuario";
import { Gerente } from "../../../shared/models/gerente.model";
import { User } from "../../../shared/models/user.model";

@Component({
  selector: 'app-inserir-gerente',
  standalone: true,
  imports: [FormsModule, CommonModule, NgxMaskDirective, SidebarComponent],
  templateUrl: './inserir-gerente.component.html',
  styleUrl: './inserir-gerente.component.css'
})
export class InserirGerenteComponent{
  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly managerService: GerenteService,
    private readonly userService: UserService
  ){
  }

  gerente: Gerente = {
    id: 0,
    nome: '',
    cpf: '',
    email: '',
    telefone: '',
    clientes: []
  }

  user: User = {
    login: '',
    senha: '',
    tipoUsuario: TipoUsuario.GERENTE,
    usuario: null
  };

  confereSenha: string = '';

  onSubmit(){
    Object.values(this.meuForm.controls).forEach(control => {
      control.markAsTouched();
    });
    
    if (this.meuForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    this.newManager();
  }

  newManager(){
    const result = this.managerService.createManager(this.gerente);
    this.user.login = this.gerente.email;
    this.user.usuario = this.gerente;

    const userResult = this.userService.createUserAccount(this.user);

    if(userResult.success && result.success){
      this.toastr.success('Gerente cadastrado com sucesso!', 'Sucesso');
    }
    else{
      this.toastr.warning(result.message, 'Erro');
    }
  }

  updateManager(){
    const result = this.managerService.updateManager(this.gerente);
    this.user.usuario = this.gerente;
    const userResult = this.userService.updateUserPassword(this.user, this.user.senha);

    if(result.success && userResult.success){
      this.toastr.success('Gerente atualizado com sucesso!', 'Sucesso');
    }
    else{
      this.toastr.warning(result.message, 'Erro');
    }
  }
}
