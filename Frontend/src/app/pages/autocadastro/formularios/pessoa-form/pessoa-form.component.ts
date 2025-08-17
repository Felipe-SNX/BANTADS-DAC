import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { CpfValidatorDirective } from '../../../../shared/directives/cpf-validator.directive';
import { InputGreaterThanZeroDirective } from '../../../../shared/directives/input-greater-than-zero.directive';

@Component({
  selector: 'pessoa-form',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxMaskDirective, CpfValidatorDirective, InputGreaterThanZeroDirective],
  templateUrl: './pessoa-form.component.html',
  styleUrl: '../../autocadastro.component.css',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }]
})
export class PessoaFormComponent {

}
