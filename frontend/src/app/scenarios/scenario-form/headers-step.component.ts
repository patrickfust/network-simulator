import { Component, inject, Input } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatStepLabel, MatStepperNext, MatStepperPrevious } from '@angular/material/stepper';
import { MatFormField, MatLabel, MatInput } from '@angular/material/input';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { ScenarioForm } from './scenario-form';
import { ScenarioHeader } from '../../models/scenario';
import { materialImports } from '../../shared/material-imports';

@Component({
  selector: 'app-headers-step',
  templateUrl: './headers-step.component.html',
  styleUrl: './headers-step.component.scss',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatButton,
    MatIconButton,
    MatIcon,
    MatStepperNext,
    MatStepperPrevious,
    MatStepLabel,
    materialImports,
  ]
})
export class HeadersStepComponent {
  @Input() formGroup!: FormGroup;
  @Input() parent!: ScenarioForm;

  private fb = inject(FormBuilder);

  get headers(): FormArray {
    return this.formGroup.get('headers') as FormArray;
  }

  addHeader(): void {
    console.log('Adding header, current count:', this.headers.length);
    this.headers.push(this.createHeaderFormGroup());
    console.log('After adding header, count:', this.headers.length);
  }

  removeHeader(index: number): void {
    this.headers.removeAt(index);
  }

  createHeaderFormGroup(header?: ScenarioHeader): FormGroup {
    return this.fb.group({
      id: [header?.id],
      headerName: [header?.headerName || '', Validators.required],
      headerValue: [header?.headerValue || '', Validators.required],
      headerReplaceValue: [header?.headerReplaceValue ?? true],
    });
  }

  getErrorMessage(fieldName: string): string {
    const control = this.formGroup.get(fieldName);
    if (!control?.errors) return '';
    const errorKey = Object.keys(control.errors)[0];
    return this.parent['getErrorMessage'](fieldName, errorKey, control.errors[errorKey]);
  }
}
