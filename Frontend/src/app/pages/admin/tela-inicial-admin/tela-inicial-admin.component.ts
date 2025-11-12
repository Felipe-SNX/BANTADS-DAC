import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { AdminData, AdminService } from '../../../services/admin/admin.service';
import {Dashboard} from "../../../shared/models/dashboard.model";



@Component({
  selector: 'app-tela-inicial-admin',
  standalone: true,
  imports: [
    SidebarComponent,
    CommonModule
  ],
  templateUrl: './tela-inicial-admin.component.html',
  styleUrl: './tela-inicial-admin.component.css'
})

export class TelaInicialAdminComponent implements OnInit {
  AdminDashboard: Dashboard[] = [];
  admin: AdminData | null = null;

  constructor (
    private readonly adminService: AdminService
  ) {}

  async ngOnInit(): Promise<void> {

    this.admin = this.adminService.getAdminData();

    this.AdminDashboard = await this.adminService.dashboardAdmin();

  }
}
