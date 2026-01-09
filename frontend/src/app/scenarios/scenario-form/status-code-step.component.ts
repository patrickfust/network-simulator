import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatStepLabel, MatStepperPrevious} from '@angular/material/stepper';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {ScenarioForm} from './scenario-form';
import {ScenarioStepBase} from './scenario-step.base';

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
export class StatusCodeStepComponent extends ScenarioStepBase {
}
