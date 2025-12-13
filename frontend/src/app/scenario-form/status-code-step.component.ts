import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatStepLabel, MatStepperPrevious} from '@angular/material/stepper';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {ScenarioForm} from './scenario-form';

@Component({
  selector: 'app-status-code-step',
  templateUrl: './status-code-step.component.html',
  standalone: true,
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
    MatError,
    MatButton,
    MatStepperPrevious,
    MatStepLabel
  ]
})
export class StatusCodeStepComponent {
  @Input() formGroup!: FormGroup;
  @Input() parent!: ScenarioForm;

  getErrorMessage(fieldName: string): string {
    const control = this.formGroup.get(fieldName);
    if (!control?.errors) return '';
    const errorKey = Object.keys(control.errors)[0];
    return this.parent['getErrorMessage'](fieldName, errorKey, control.errors[errorKey]);
  }

}
