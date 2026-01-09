import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatStepLabel, MatStepperNext, MatStepperPrevious} from '@angular/material/stepper';
import {MatError, MatFormField, MatHint, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {ScenarioForm} from './scenario-form';
import {ScenarioStepBase} from './scenario-step.base';

@Component({
  selector: 'app-network-step',
  templateUrl: './network-step.component.html',
  standalone: true,
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
    MatError,
    MatButton,
    MatStepperNext,
    MatStepperPrevious,
    MatStepLabel,
    MatHint
  ]
})
export class NetworkStepComponent extends ScenarioStepBase {
}
