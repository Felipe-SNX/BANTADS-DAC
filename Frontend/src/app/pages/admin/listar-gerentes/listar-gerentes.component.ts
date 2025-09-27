import { Component, inject, Inject, OnInit } from '@angular/core';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { Gerente } from '../../../shared/models/gerente.model';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { CommonModule, DOCUMENT } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxMaskPipe } from 'ngx-mask';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-listar-gerentes',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskPipe],
  templateUrl: './listar-gerentes.component.html',
  styleUrl: './listar-gerentes.component.css'
})
export class ListarGerentesComponent implements OnInit{
  private readonly toastr = inject(ToastrService);
  gerentes: Gerente[] = [];

  constructor(
    private readonly managerService: GerenteService,
    private readonly router: Router,
    @Inject(DOCUMENT) private readonly document: Document
  ) { }


  ngOnInit(): void {
    const managers = this.managerService.listManagers();

    managers.sort((a, b) => {
      const nameA = a.nome.toUpperCase(); 
      const nameB = b.nome.toUpperCase(); 
      if (nameA < nameB) {
        return -1;
      }
      if (nameA > nameB) {
        return 1;
      }

      return 0;
    });

    this.gerentes = managers;
  }

  deletarGerente(gerente: Gerente){
    this.managerService.deleteManager(gerente.id);
    this.toastr.success('Gerente deletado com sucesso!', 'Sucesso');
    this.document.defaultView?.location.reload();
  }

  editarGerente(gerente: Gerente){
    this.router.navigate(['admin/editarGerente', gerente.id]);
  }

}
