import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NgxMaskPipe } from 'ngx-mask';
import { Conta } from '../../../shared/models/conta.model';
import { ContaService } from '../../../services/conta/conta.service';
import { ActivatedRoute, Router } from '@angular/router';
import { GerenteService } from '../../../services/gerente/gerente.service';

@Component({
  selector: 'app-listar-clientes-gerente',
  templateUrl: './listar-clientes-gerente.component.html',
  styleUrls: ['./listar-clientes-gerente.component.css'],
  imports: [CommonModule, FormsModule, SidebarComponent, NgxMaskPipe],
  standalone: true
})
export class ListarClientesGerenteComponent implements OnInit {
  listaId: number = 0;
  contas: Conta[] = [];
  filtroNome: string = '';
  filtroCpf: string = '';

  constructor(
    private contaService: ContaService,
    private route: ActivatedRoute,
    private router: Router,
    private managerService: GerenteService
  ) { }

  ngOnInit() {
    this.listaId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadContas();
  }

  loadContas() {
    const gerenteId = this.managerService.findLoggedUser();
    const accounts = this.contaService.listAccountsByManager(gerenteId as number);

    if (this.listaId === 1) {
      accounts.sort((a, b) => {
        const nameA = a.cliente.nome.toUpperCase();
        const nameB = b.cliente.nome.toUpperCase();
        if (nameA < nameB) return -1;
        if (nameA > nameB) return 1;
        return 0;
      });
    } else if (this.listaId === 3) {
      accounts.sort((a, b) => b.saldo - a.saldo);
      accounts.splice(3);
    }

    this.contas = accounts;
  }

  get contasFiltradas(): Conta[] {
    return this.contas.filter(conta => {
      const nomeMatch = this.filtroNome
        ? conta.cliente.nome.toLowerCase().includes(this.filtroNome.toLowerCase())
        : true;
      const cpfMatch = this.filtroCpf
        ? conta.cliente.cpf.replace(/\D/g, '').includes(this.filtroCpf.replace(/\D/g, ''))
        : true;
      return nomeMatch && cpfMatch;
    });
  }

  irParaConsulta(clienteId: number) {
    this.router.navigate(['cliente/consulta', clienteId]);
  }
}
