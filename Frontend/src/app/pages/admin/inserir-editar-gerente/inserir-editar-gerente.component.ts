import { CommonModule } from "@angular/common";
import { Component, OnInit, ViewChild, inject } from "@angular/core";
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
  templateUrl: './inserir-editar-gerente.component.html',
  styleUrl: './inserir-editar-gerente.component.css'
})
export class InserirGerenteComponent implements OnInit{
  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  id: number = 0;
  tipoTela: string = 'Novo';

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

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    if(this.id !== 0){
      this.tipoTela = 'Editar';
      const manager = this.managerService.listManagerById(this.id);

      if(!manager){
        console.error('Gerente não encontrado');
        return;
      }

      const usuario = this.userService.findUserByLogin(manager.email);

      if(!usuario){
        console.error('Usuário não encontrado');
        return;
      }

      this.user = usuario;
      this.gerente = manager;
    }
  }

  onSubmit(){
    Object.values(this.meuForm.controls).forEach(control => {
      control.markAsTouched();
    });
    
    if (this.meuForm.invalid) {
      this.toastr.error('Corrija os erros do formulário', 'Erro');
      return;
    }

    if(this.tipoTela === 'Novo'){
      this.newManager();
    }

    else{
      this.updateManager()
    }
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
