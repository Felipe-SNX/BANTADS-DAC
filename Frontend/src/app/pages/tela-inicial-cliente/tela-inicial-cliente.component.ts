import { Component, OnInit } from '@angular/core';
import { MockDataService } from '../../services/mock/mock-data.service';
import { Cliente } from '../../shared/models/cliente.model';
import { Conta } from '../../shared/models/conta.model';
import { Transacao } from '../../shared/models/transacao.model';
import { ActivatedRoute } from '@angular/router';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-tela-inicial-cliente',
  templateUrl: './tela-inicial-cliente.component.html',
  styleUrls: ['./tela-inicial-cliente.component.css'],
  imports: [SidebarComponent],
  standalone: true
})
export class TelaInicialClienteComponent implements OnInit {
  cliente: Cliente = new Cliente();
  conta: Conta = new Conta();
  transacoesRecentes: Transacao[] = [];
  saldoNegativo: boolean = false;


  constructor
  (private mockDataService: MockDataService,
    private route: ActivatedRoute,
  ) {

   }

  ngOnInit(): void {        
    this.mockDataService.loadMockData();

    this.route.paramMap.subscribe(params => {
    const clienteId = Number(params.get('id'));

    if (clienteId)
        this.loadClienteData(clienteId);
    });
    
    const clientes = JSON.parse(localStorage.getItem('clientes') || '[]');
    const contas = JSON.parse(localStorage.getItem('contas') || '[]');
    const movimentacoes = JSON.parse(localStorage.getItem('movimentacoes') || '[]');
    
    if (clientes.length > 0) {       
            
      this.conta = contas.find((conta: any) => conta.cliente.id === this.cliente.id);
            
      this.saldoNegativo = this.conta && this.conta.saldo < 0;                  
    }
  }

  onActionSelected(action: string) {    
  }

   loadClienteData(clienteId: number): void {    
      const clientes = JSON.parse(localStorage.getItem('clientes') || '[]');
      const contas = JSON.parse(localStorage.getItem('contas') || '[]');
      const movimentacoes = JSON.parse(localStorage.getItem('movimentacoes') || '[]');

      this.cliente = clientes.find((c: any) => c.id === clienteId);      
            
      this.conta = contas.find((conta: any) => conta.cliente.id === clienteId);            
          
      this.saldoNegativo = this.conta.saldo < 0;    
  }
}