import { Component, inject, Inject, OnInit } from '@angular/core';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { Gerente } from '../../../shared/models/gerente.model';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { CommonModule, DOCUMENT } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxMaskPipe } from 'ngx-mask';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { DadoGerente } from '../../../shared/models/dado-gerente.model';
import {LoadingComponent} from "../../../shared/components/loading/loading.component";

@Component({
  selector: 'app-listar-gerentes',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskPipe, LoadingComponent],
  templateUrl: './listar-gerentes.component.html',
  styleUrl: './listar-gerentes.component.css'
})
export class ListarGerentesComponent implements OnInit{
  private readonly toastr = inject(ToastrService);
  gerentes: DadoGerente[] = [];
  loading: boolean = true;

  constructor(
    private readonly managerService: GerenteService,
    private readonly router: Router,
    @Inject(DOCUMENT) private readonly document: Document
  ) { }


  ngOnInit(): void {
    this.listarGerentes();
  }

  private async listarGerentes(): Promise<void> {
    try {
      this.loading = true;
      this.gerentes = await this.managerService.listarGerentes();
      this.loading = false;
    } catch (error) {
      this.loading = false;
      console.error(error);
    }
  }

   async deletarGerente(gerente: DadoGerente){
    await this.managerService.deleteManager(gerente.cpf);
    this.toastr.success('Gerente deletado com sucesso!', 'Sucesso');
    this.gerentes = this.gerentes.filter(g => g.cpf !== gerente.cpf);
  }

  editarGerente(gerente: DadoGerente){
    this.router.navigate(['admin/editarGerente', gerente.cpf]);
  }

}
