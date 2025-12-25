import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatStepperNext} from '@angular/material/stepper';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatError, MatFormField, MatHint, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {ScenarioForm} from './scenario-form';

@Component({
  selector: 'app-basic-info-step',
  templateUrl: './basic-info-step.component.html',
  standalone: true,
  imports: [
    MatSlideToggle,
    MatFormField,
    MatLabel,
    MatInput,
    MatHint,
    ReactiveFormsModule,
    MatError,
    MatButton,
    MatStepperNext
  ]
})
export class BasicInfoStepComponent {
  @Input() formGroup!: FormGroup;
  @Input() parent!: ScenarioForm;

  getErrorMessage(fieldName: string): string {
    const control = this.formGroup.get(fieldName);
    if (!control?.errors) return '';
    const errorKey = Object.keys(control.errors)[0];
    return this.parent['getErrorMessage'](fieldName, errorKey, control.errors[errorKey]);
  }

}
