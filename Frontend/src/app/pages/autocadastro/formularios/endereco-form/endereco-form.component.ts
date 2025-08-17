import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { InputGreaterThanZeroDirective } from '../../../../shared/directives/input-greater-than-zero.directive';

@Component({
  selector: 'endereco-form',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxMaskDirective, InputGreaterThanZeroDirective],
  templateUrl: './endereco-form.component.html',
  styleUrl: '../../autocadastro.component.css',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }]
})
export class EnderecoFormComponent {

}
