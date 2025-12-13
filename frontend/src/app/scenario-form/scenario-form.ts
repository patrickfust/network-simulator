import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Scenario} from '../models/scenario';
import {MaterialModule} from '../shared/material.module';
import {BasicInfoStepComponent} from './basic-info-step.component';
import {NetworkStepComponent} from './network-step.component';
import {MatList, MatListItem} from '@angular/material/list';
import {StatusCodeStepComponent} from './status-code-step.component';
import {MatCard} from '@angular/material/card';
import {MatDialog} from '@angular/material/dialog';
import {DeleteScenarioDialog} from './dialog-delete-scenario';

@Component({
  selector: 'app-scenario-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    BasicInfoStepComponent,
    NetworkStepComponent,
    MatList,
    MatListItem,
    StatusCodeStepComponent,
    MatCard,
  ],
  templateUrl: './scenario-form.html',
  styleUrl: './scenario-form.scss',
})
export class ScenarioForm implements OnInit, OnChanges {
  readonly dialog = inject(MatDialog);

  @Input() scenario?: Scenario;
  @Input() submitButtonText: string = 'Submit';
  @Output() formSubmit = new EventEmitter<Scenario>();
  @Output() deleteScenarioSubmit = new EventEmitter<Scenario>();

  basicInfoFormGroup: FormGroup;
  networkFormGroup: FormGroup;
  statusCodeFormGroup: FormGroup;
  isLinear = false;

  constructor(private fb: FormBuilder) {
    this.basicInfoFormGroup = fb.group({
      name: ['', Validators.required],
      path: [''],
      description: [''],
      enableScenario: [true],
    });
    this.networkFormGroup = fb.group({
      latencyMs: ['', [Validators.min(0), Validators.pattern(/^\d+$/)]],
      timeoutMs: ['', [Validators.min(0), Validators.pattern(/^\d+$/)]],
    });
    this.statusCodeFormGroup = fb.group({
      statusCode: ['', [Validators.min(100), Validators.max(599), Validators.pattern(/^\d+$/)]],
      responseBody: [''],
    }, {validators: this.statusCodeBodyValidator.bind(this)});
  }

  ngOnInit(): void {
    if (this.scenario) {
      this.populateForm(this.scenario);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Only patch if forms are initialized and we have a valid scenario
    if (changes['scenario'] && changes['scenario'].currentValue && this.basicInfoFormGroup && this.networkFormGroup) {
      this.populateForm(changes['scenario'].currentValue);
    }
  }

  private populateForm(scenario: Scenario): void {
    // Add null checks before patching
    if (!this.basicInfoFormGroup || !this.networkFormGroup) {
      return;
    }

    this.basicInfoFormGroup.patchValue({
      name: scenario.name || '',
      path: scenario.path || '',
      description: scenario.description || '',
      enableScenario: scenario.enableScenario ?? true,
    }, {emitEvent: false});

    this.networkFormGroup.patchValue({
      latencyMs: scenario.latencyMs ?? '',
      timeoutMs: scenario.timeoutMs ?? '',
    }, {emitEvent: false});

    this.statusCodeFormGroup.patchValue({
      statusCode: scenario.statusCode ?? '',
      responseBody: scenario.responseBody ?? '',
    }, {emitEvent: false});
  }

  onSubmit(): void {
    if (this.basicInfoFormGroup.valid && this.networkFormGroup.valid) {
      const basic = this.basicInfoFormGroup.value;
      const network = this.networkFormGroup.value;
      const statusCode = this.statusCodeFormGroup.value;

      const scenario: Scenario = {
        id: this.scenario?.id,
        ...basic,
        ...network,
        ...statusCode,
        latencyMs: network.latencyMs !== '' && network.latencyMs != null ? Number(network.latencyMs) : undefined,
        timeoutMs: network.timeoutMs !== '' && network.timeoutMs != null ? Number(network.timeoutMs) : undefined,
      };

      this.formSubmit.emit(scenario);
    } else {
      this.basicInfoFormGroup.markAllAsTouched();
      this.networkFormGroup.markAllAsTouched();
    }
  }

  getFormErrors(): string[] {
    const errors: string[] = [];
    this.checkForErrors('Basic scenario information', this.basicInfoFormGroup, errors);
    this.checkForErrors('Network simulation', this.networkFormGroup, errors);
    this.checkForErrors('Status Code', this.statusCodeFormGroup, errors);
    return errors;
  }

  private checkForErrors(groupName: string, formGroup: FormGroup, errors: string[]) {
    // Check form group level errors
    if (formGroup.errors) {
      Object.keys(formGroup.errors).forEach(errorKey => {
        const message = this.getErrorMessage('', errorKey, formGroup.errors![errorKey]);
        errors.push(`${groupName}: ${message}`);
      });
    }

    // Check control level errors
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      if (control?.errors) {
        Object.keys(control.errors).forEach(errorKey => {
          const message = this.getErrorMessage(key, errorKey, control.errors![errorKey]);
          errors.push(`${groupName}: ${message}`);
        });
      }
    });
  }

  private getErrorMessage(fieldName: string, errorKey: string, errorValue: any): string {
    const fieldLabel = this.getFieldLabel(fieldName);

    switch (errorKey) {
      case 'required':
        return `${fieldLabel} is required`;
      case 'min':
        return `${fieldLabel} must be at least ${errorValue.min}`;
      case 'max':
        return `${fieldLabel} must be at most ${errorValue.max}`;
      case 'pattern':
        return `${fieldLabel} must be a valid number`;
      case 'bodyWithoutStatusCode':
        return 'Response Body cannot be set without a Status Code';
      default:
        return `${fieldLabel} has an error: ${errorKey}`;
    }
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      'name': 'Name',
      'path': 'Path',
      'description': 'Description',
      'enableScenario': 'Enable Scenario',
      'latencyMs': 'Latency (ms)',
      'timeoutMs': 'Timeout (ms)',
      'statusCode': 'Status Code',
      'responseBody': 'Response Body',
    };

    return labels[fieldName] || fieldName;
  }

  private statusCodeBodyValidator(control: AbstractControl): ValidationErrors | null {
    const statusCode = control.get('statusCode')?.value;
    const body = control.get('responseBody')?.value;
    if (!statusCode && body) {
      return {bodyWithoutStatusCode: true};
    }

    return null;
  }

  showDeleteScenarioDialog(): void {
    const dialogRef = this.dialog.open(DeleteScenarioDialog, {
      data: {scenario: this.scenario},
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result == true) {
        this.deleteScenarioSubmit.emit(this.scenario);
      }
    });
  }

}
