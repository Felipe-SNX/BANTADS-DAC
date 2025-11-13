import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NgxMaskPipe } from 'ngx-mask';
import {ClienteResponse} from "../../../shared/models/cliente-response.model";
import {ClienteService} from "../../../services/cliente/cliente.service";

@Component({
  selector: 'app-relatorio-clientes',
  standalone: true,
  imports: [
    CommonModule,
    SidebarComponent,
    NgxMaskPipe
  ],
  templateUrl: './relatorio-clientes.component.html',
  styleUrl: './relatorio-clientes.component.css'
})
export class RelatorioClientesComponent implements OnInit {

  clientes: ClienteResponse[] = [];

  constructor(
    private readonly clienteService: ClienteService,
  ) { }

  async ngOnInit(): Promise<void> {
    this.clientes = await this.clienteService.relatorioClientes();
  }

}
