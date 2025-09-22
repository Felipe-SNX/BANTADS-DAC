import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../../../services/cliente/cliente.service';
import { CommonModule } from '@angular/common';
import { ClientData } from '../../../services/cliente/cliente.service';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';


@Component({
  selector: 'app-relatorio-clientes',
  standalone: true,
  imports: [
    CommonModule,
    SidebarComponent,
    
  ],
  templateUrl: './relatorio-clientes.component.html',
  styleUrl: './relatorio-clientes.component.css'
})
export class RelatorioClientesComponent implements OnInit {

  clientes: ClientData[] = [];

  constructor(
    private readonly clienteService: ClienteService, 
  ) { }

  ngOnInit(): void {
    this.clientes = this.clienteService.listClientData();
  }
  
}
