import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'endereco-form',
  standalone: true,
  imports: [CommonModule, FormsModule, NgxMaskDirective],
  templateUrl: './endereco-form.component.html',
  styleUrl: '../../autocadastro.component.css',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }]
})
export class EnderecoFormComponent {
    @Input() endereco!: {
      cep: string,
      tipo: string,
      logradouro: string,
      numero: number,
      complemento: string,
      cidade: string,
      estado: string
    };

    constructor(){}
}
