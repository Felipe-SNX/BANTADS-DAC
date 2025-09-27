import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { AdminData, AdminDashboard, AdminService } from '../../../services/admin/admin.service';



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
  AdminDashboard: AdminDashboard[] = [];
  admin: AdminData | null = null;

  constructor (
    private readonly adminService: AdminService
  ) {} 

  ngOnInit(): void {

    this.admin = this.adminService.getAdminData();
    
    this.AdminDashboard = this.adminService.getAdminDashboardData();

  }
} 