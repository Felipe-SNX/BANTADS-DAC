/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { TelaInicialGerenteComponent } from './tela-inicial-gerente.component';

describe('TelaInicialGerenteComponent', () => {
  let component: TelaInicialGerenteComponent;
  let fixture: ComponentFixture<TelaInicialGerenteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TelaInicialGerenteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TelaInicialGerenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
