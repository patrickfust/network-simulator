import {Directive, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {ScenarioForm} from './scenario-form';
import {NetworkSimulatorForm} from '../../shared/forms/network-simulator-form';

@Directive()
export abstract class ScenarioStepBase extends NetworkSimulatorForm {
  @Input() formGroup!: FormGroup;
  @Input() parent!: ScenarioForm;

  public getErrorMessage(fieldName: string): string {
    const control = this.formGroup.get(fieldName);
    if (!control?.errors) return '';
    const errorKey = Object.keys(control.errors)[0];
    return this.parent.getErrorMessage(fieldName, errorKey, control.errors[errorKey]);
  }

}
