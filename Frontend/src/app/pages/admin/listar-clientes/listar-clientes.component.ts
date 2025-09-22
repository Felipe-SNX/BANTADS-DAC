import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NgxMaskPipe } from 'ngx-mask';
import { Conta } from '../../../shared/models/conta.model';
import { ContaService } from '../../../services/conta/conta.service';

@Component({
  selector: 'app-listar-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskPipe],
  templateUrl: './listar-clientes.component.html',
  styleUrl: './listar-clientes.component.css'
})
export class ListarClientesComponent implements OnInit{
  contas: Conta[] = [];

  constructor(
    private readonly accountService: ContaService,
  ) { }


  ngOnInit(): void {
    const accounts = this.accountService.listAccounts();;

    accounts.sort((a, b) => {
      const nameA = a.cliente.nome.toUpperCase(); 
      const nameB = b.cliente.nome.toUpperCase(); 
      if (nameA < nameB) {
        return -1;
      }
      if (nameA > nameB) {
        return 1;
      }

      return 0;
    });

    this.contas = accounts;
  }

}
