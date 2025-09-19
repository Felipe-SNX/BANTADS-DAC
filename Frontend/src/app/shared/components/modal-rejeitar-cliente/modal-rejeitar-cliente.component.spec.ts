import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalRejeitarClienteComponent } from './modal-rejeitar-cliente.component';

describe('ModalRejeitarClienteComponent', () => {
  let component: ModalRejeitarClienteComponent;
  let fixture: ComponentFixture<ModalRejeitarClienteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalRejeitarClienteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalRejeitarClienteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
