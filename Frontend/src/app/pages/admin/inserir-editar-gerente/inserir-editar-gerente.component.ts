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
import {DadoGerente} from "../../../shared/models/dado-gerente.model";
import {DadoGerenteInsercao} from "../../../shared/models/dado-gerente-insercao.model";
import {LoadingComponent} from "../../../shared/components/loading/loading.component";

@Component({
  selector: 'app-inserir-gerente',
  standalone: true,
  imports: [FormsModule, CommonModule, NgxMaskDirective, SidebarComponent, LoadingComponent],
  templateUrl: './inserir-editar-gerente.component.html',
  styleUrl: './inserir-editar-gerente.component.css'
})
export class InserirGerenteComponent implements OnInit{
  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  loading: boolean = true;
  cpf: string = '';
  editMode: boolean = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly managerService: GerenteService,
    private readonly router: Router
  ){
  }

  gerente: DadoGerenteInsercao = new DadoGerenteInsercao();

  confereSenha: string = '';

  async ngOnInit(): Promise<void> {
    this.loading = true;
    this.cpf = this.route.snapshot.params['cpf'];

    if(this.cpf !== '' && this.cpf !== undefined){
      this.editMode = true;
      const manager = await this.managerService.getGerente(this.cpf);

      if(!manager){
        console.error('Gerente não encontrado');
        return;
      }

      this.gerente.cpf = manager.cpf;
      this.gerente.tipo = manager.tipo;
      this.gerente.nome = manager.nome;
      this.gerente.email = manager.email;
      this.gerente.telefone = manager.telefone;
    }
    this.loading = false;
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
    try {
      this.managerService.saveGerente(this.gerente);
      this.toastr.success('Gerente cadastrado com sucesso!', 'Sucesso');
      this.router.navigate(['admin/listarGerentes']);
    } catch(error){
      this.toastr.error('Erro ao cadastrar gerente', 'Erro');
    }
  }

  updateManager(){
    try {
      this.managerService.updateManager(this.gerente, this.gerente.cpf);
      this.toastr.success('Gerente atualizado com sucesso!', 'Sucesso');
      this.router.navigate(['admin/listarGerentes']);
    }catch(error){
      this.toastr.warning("Erro ao atualizar gerente", 'Erro');
    }
  }
}
