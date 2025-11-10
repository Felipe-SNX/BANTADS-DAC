import { CommonModule } from "@angular/common";
import { Component, OnInit, ViewChild, inject } from "@angular/core";
import { FormsModule, NgForm } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { NgxMaskDirective } from "ngx-mask";
import { ToastrService } from "ngx-toastr";
import { UserService } from "../../../services/user/user.service";
import { GerenteService } from "../../../services/gerente/gerente.service";
import { SidebarComponent } from "../../../shared/components/sidebar/sidebar.component";
import { TipoUsuario } from "../../../shared/enums/TipoUsuario";
import { Gerente } from "../../../shared/models/gerente.model";
import { User } from "../../../shared/models/user.model";

@Component({
  selector: 'app-inserir-gerente',
  standalone: true,
  imports: [FormsModule, CommonModule, NgxMaskDirective, SidebarComponent, RouterLink],
  templateUrl: './inserir-editar-gerente.component.html',
  styleUrl: './inserir-editar-gerente.component.css'
})
export class InserirGerenteComponent implements OnInit{
  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  id: number = 0;
  editMode: boolean = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly managerService: GerenteService,
    private readonly userService: UserService,
    private readonly router: Router
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
    cpf: '',
    id: 0
  };

  confereSenha: string = '';

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    if(this.id !== 0 && this.id !== undefined){
      this.editMode = true;
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

    if(!this.editMode){
      this.newManager();
    }
    else{
      this.updateManager()
    }
  }

  newManager(){
    const result = this.managerService.createManager(this.gerente);
    this.user.login = this.gerente.email;
    this.user.id = this.gerente.id;

    const userResult = this.userService.createUserAccount(this.user);

    if(userResult.success && result.success){
      this.toastr.success('Gerente cadastrado com sucesso!', 'Sucesso');
      this.router.navigate(['admin/listarGerentes']);
    }
    else{
      this.toastr.error(result.message, 'Erro');
    }

  }

  updateManager(){
    const result = this.managerService.updateManager(this.gerente);
    const userResult = this.userService.updateUserPassword(this.user, this.user.senha);

    if(result.success && userResult.success){
      this.toastr.success('Gerente atualizado com sucesso!', 'Sucesso');
      this.router.navigate(['admin/listarGerentes']);
    }
    else{
      this.toastr.warning(result.message, 'Erro');
    }
  }
}
