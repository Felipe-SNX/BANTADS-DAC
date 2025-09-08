import { Component, OnInit } from '@angular/core';
import { Conta } from '../../shared/models/conta.model';
import { MockDataService } from '../../services/mock/mock-data.service';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';


@Component({
  selector: 'app-tela-inicial-gerente',
  templateUrl: './tela-inicial-gerente.component.html',
  styleUrls: ['./tela-inicial-gerente.component.css'],
  standalone: true,
  imports: [CommonModule, SidebarComponent]
})
export class TelaInicialGerenteComponent implements OnInit {
  contasParaAprovar: Conta[] = [];
  gerenteId: number = 0;

  constructor(
    private mockDataService: MockDataService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.mockDataService.loadMockData();

    this.route.paramMap.subscribe(params => {
      this.gerenteId = Number(params.get('id'));
      const contas = JSON.parse(localStorage.getItem('contas') || '[]');
      this.contasParaAprovar = contas.filter(
        (conta: any) => conta.gerente && conta.gerente.id === this.gerenteId
      );
    });
  }

  aprovar(conta: Conta) {
    // conta.aprovada = true;
    this.atualizarLocalStorage(conta);
    this.contasParaAprovar = this.contasParaAprovar.filter(c => c.numConta !== conta.numConta);
  }

  recusar(conta: Conta) {
    this.contasParaAprovar = this.contasParaAprovar.filter(c => c.numConta !== conta.numConta);
    let contas = JSON.parse(localStorage.getItem('contas') || '[]');
    contas = contas.filter((c: any) => c.numConta !== conta.numConta);
    localStorage.setItem('contas', JSON.stringify(contas));
  }

  private atualizarLocalStorage(contaAprovada: Conta) {
    let contas = JSON.parse(localStorage.getItem('contas') || '[]');
    contas = contas.map((c: any) => {
      if (c.numConta === contaAprovada.numConta) {
        // c.aprovada = true;
      }
      return c;
    });
    localStorage.setItem('contas', JSON.stringify(contas));
  }

  onActionSelected(action: string) {    
  }
}