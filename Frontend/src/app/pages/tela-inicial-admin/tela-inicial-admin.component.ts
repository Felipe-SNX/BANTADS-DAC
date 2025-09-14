import { Component } from '@angular/core';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-tela-inicial-admin',
  standalone: true,
  imports: [SidebarComponent],
  templateUrl: './tela-inicial-admin.component.html',
  styleUrl: './tela-inicial-admin.component.css'
})
export class TelaInicialAdminComponent {

}
