import { CommonModule } from "@angular/common";
import { Component, OnInit, ViewChild, inject } from "@angular/core";
import { FormsModule, NgForm } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { NgxMaskDirective } from "ngx-mask";
import { ToastrService } from "ngx-toastr";
import { GerenteService } from "../../../services/gerente/gerente.service";
import { SidebarComponent } from "../../../shared/components/sidebar/sidebar.component";
import { DadoGerenteInsercao } from "../../../shared/models/dado-gerente-insercao.model";
import { LoadingComponent } from "../../../shared/components/loading/loading.component";

@Component({
  selector: 'app-inserir-gerente',
  standalone: true,
  imports: [FormsModule, CommonModule, NgxMaskDirective, SidebarComponent, LoadingComponent],
  templateUrl: './inserir-editar-gerente.component.html',
  styleUrl: './inserir-editar-gerente.component.css'
})
export class InserirGerenteComponent implements OnInit {
  @ViewChild('meuForm') meuForm!: NgForm;
  private readonly toastr = inject(ToastrService);

  loading: boolean = false; 
  cpf: string = '';
  editMode: boolean = false;

  gerente: DadoGerenteInsercao = new DadoGerenteInsercao();
  confereSenha: string = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly managerService: GerenteService,
    private readonly router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    this.cpf = this.route.snapshot.params['cpf'];

    if (this.cpf) {
      this.editMode = true;
      await this.loadManagerData(this.cpf);
    }
  }

  private async loadManagerData(cpf: string) {
    this.loading = true; 
    try {
      const manager = await this.managerService.getGerente(cpf);

      if (!manager) {
        this.toastr.error('Gerente não encontrado na base de dados.');
        this.router.navigate(['admin/listarGerentes']);
        return;
      }

      this.gerente.cpf = manager.cpf;
      this.gerente.tipo = manager.tipo;
      this.gerente.nome = manager.nome;
      this.gerente.email = manager.email;
      this.gerente.telefone = manager.telefone;

    } catch (error: any) {
      const dados = error.response.data;
      const msgBackend = dados?.message || dados?.error || JSON.stringify(dados);
      this.toastr.error(msgBackend, 'Erro');
      this.router.navigate(['admin/listarGerentes']);
    } finally {
      this.loading = false; 
    }
  }

  async onSubmit() {
    if (this.meuForm) {
      Object.values(this.meuForm.controls).forEach(control => {
        control.markAsTouched();
      });

      if (this.meuForm.invalid) {
        this.toastr.error('Corrija os erros do formulário', 'Formulário Inválido');
        return;
      }
    }

    if (!this.editMode && this.gerente.senha !== this.confereSenha) {
       this.toastr.error('As senhas não conferem', 'Erro');
       return;
    }

    if (!this.editMode) {
      await this.newManager();
    } else {
      await this.updateManager();
    }
  }

  async newManager() {
    this.loading = true;
    try {
      await this.managerService.saveGerente(this.gerente);
      
      this.toastr.success('Gerente cadastrado com sucesso!', 'Sucesso');
      this.router.navigate(['admin/listarGerentes']);
    } catch (error: any) {
      const dados = error.response.data;
      const msgBackend = dados?.message || dados?.error || JSON.stringify(dados);
      this.toastr.error(msgBackend, 'Erro');
    } finally {
      this.loading = false;
    }
  }

  async updateManager() {
    this.loading = true;
    try {
      await this.managerService.updateManager(this.gerente, this.gerente.cpf);
      
      this.toastr.success('Gerente atualizado com sucesso!', 'Sucesso');
      this.router.navigate(['admin/listarGerentes']);
    } catch (error: any) {
      const dados = error.response.data;
      const msgBackend = dados?.message || dados?.error || JSON.stringify(dados);
      this.toastr.error(msgBackend, 'Erro');
    } finally {
      this.loading = false;
    }
  }
}