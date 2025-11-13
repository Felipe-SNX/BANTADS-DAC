import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { Dashboard } from "../../../shared/models/dashboard.model";
import { GerenteService } from '../../../services/gerente/gerente.service';
import { GerentesResponse } from '../../../shared/models/gerentes-response.model';
import {LoadingComponent} from "../../../shared/components/loading/loading.component";

@Component({
  selector: 'app-tela-inicial-admin',
  standalone: true,
  imports: [
    SidebarComponent,
    CommonModule,
    LoadingComponent
  ],
  templateUrl: './tela-inicial-admin.component.html',
  styleUrl: './tela-inicial-admin.component.css'
})

export class TelaInicialAdminComponent implements OnInit {
  loading: boolean = true;
  AdminDashboard: Dashboard[] = [];
  admin: GerentesResponse | null = null;

  constructor (
    private readonly gerenteService: GerenteService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private async loadDashboardData(): Promise<void> {
    try {
      this.loading = true;
      this.AdminDashboard = await this.gerenteService.dashboardAdmin();
      this.loading = false;
    } catch (error) {
      this.loading = false;
      console.error(error);
    }
  }
}
