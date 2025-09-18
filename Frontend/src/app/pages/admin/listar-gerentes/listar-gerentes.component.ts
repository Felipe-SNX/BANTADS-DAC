import { Component, OnInit } from '@angular/core';
import { GerenteService } from '../../../services/gerente/gerente.service';
import { Gerente } from '../../../shared/models/gerente.model';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-listar-gerentes',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskDirective],
  templateUrl: './listar-gerentes.component.html',
  styleUrl: './listar-gerentes.component.css'
})
export class ListarGerentesComponent implements OnInit{
  gerentes: Gerente[] = [];

  constructor(private readonly managerService: GerenteService) { }


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

}
