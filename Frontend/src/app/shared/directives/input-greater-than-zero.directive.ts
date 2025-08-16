import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[inputGreaterThanZero]',
  standalone: true,
  providers: [{
      provide: NG_VALIDATORS,
      useExisting: InputGreaterThanZeroDirective,
      multi: true
    }]
})
export class InputGreaterThanZeroDirective implements Validator{

  constructor() { }
  
  validate(control: AbstractControl): ValidationErrors | null {
    const valor = control.value;

    //Retorna direto se o campo está vazio
    if (!valor) {
      return { required: true }
    }

    //remove caracteres não númericos do valor
    const valorString = String(valor).replace(',', '.').replace(/[\s\W_]/g, '');
    const valorNumerico = parseFloat(valorString);

    if (isNaN(valorNumerico)) {
      return { naoENumero: true };
    }

    if (valorNumerico <= 0) {
      return { maiorQueZero: true };
    }

    // Se chegou aqui é válido 
    return null;
  }

}
