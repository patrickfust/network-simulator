import {Component, Input} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {MatStepperNext} from '@angular/material/stepper';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatError, MatFormField, MatHint, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatOption, MatSelect} from '@angular/material/select';
import {TargetSystem} from '../../models/target-system';
import {ScenarioStepBase} from './scenario-step.base';

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
    MatStepperNext,
    MatSelect,
    MatOption
  ]
})
export class BasicInfoStepComponent extends ScenarioStepBase {

  @Input() targetSystems: TargetSystem[] = [];

}
