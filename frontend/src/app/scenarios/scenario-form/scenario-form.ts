import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {
  AbstractControl, FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Scenario} from '../../models/scenario';
import {MaterialModule} from '../../shared/material.module';
import {BasicInfoStepComponent} from './basic-info-step.component';
import {NetworkStepComponent} from './network-step.component';
import {MatList, MatListItem} from '@angular/material/list';
import {StatusCodeStepComponent} from './status-code-step.component';
import {ThrottlingStepComponent} from './throttling-step.component';
import {MatCard} from '@angular/material/card';
import {MatDialog} from '@angular/material/dialog';
import {DeleteScenarioDialog} from './dialog-delete-scenario';
import {HeadersStepComponent} from './headers-step.component';
import {TargetSystemService} from '../../services/target-system.service';
import {TargetSystem} from '../../models/target-system';
import {NetworkSimulatorForm} from '../../shared/forms/network-simulator-form';

@Component({
  selector: 'app-scenario-form',
  standalone: true,
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
    HeadersStepComponent,
    ThrottlingStepComponent,
  ],
  templateUrl: './scenario-form.html',
  styleUrls: ['./scenario-form.scss'],
})
export class ScenarioForm extends NetworkSimulatorForm implements OnInit, OnChanges {
  readonly dialog = inject(MatDialog);
  targetSystemService = inject(TargetSystemService);

  @Input() scenario?: Scenario;
  @Input() submitButtonText: string = 'Submit';
  @Output() formSubmit = new EventEmitter<Scenario>();
  @Output() deleteScenarioSubmit = new EventEmitter<Scenario>();

  basicInfoFormGroup: FormGroup;
  networkFormGroup: FormGroup;
  statusCodeFormGroup: FormGroup;
  headerFormGroup: FormGroup;
  throttlingFormGroup: FormGroup;
  isLinear = false;
  targetSystems: TargetSystem[] = [];

  constructor(private fb: FormBuilder) {
    super();
    this.basicInfoFormGroup = fb.group({
      name: ['', Validators.required],
      targetSystemId: [null],
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
    this.headerFormGroup = fb.group({
      headers: this.fb.array([])
    });
    this.throttlingFormGroup = fb.group({
      responseBytesPerSecond: ['', [Validators.min(1), Validators.pattern(/^\d+$/)]],
    })
  }

  ngOnInit(): void {
    this.targetSystemService.getAllTargetSystems().subscribe({
      next: (systems) => {
        this.targetSystems = systems;
        if (this.scenario) {
          this.populateForm(this.scenario);
        }
      },
      error: (err) => {
        console.error('Failed to load target systems', err);
      }
    });  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['scenario'] && changes['scenario'].currentValue && this.basicInfoFormGroup && this.networkFormGroup && this.throttlingFormGroup) {
      this.populateForm(changes['scenario'].currentValue);
    }
  }

  private populateForm(scenario: Scenario): void {
    if (!this.basicInfoFormGroup || !this.networkFormGroup || !this.throttlingFormGroup) {
      return;
    }

    this.basicInfoFormGroup.patchValue({
      name: scenario.name || '',
      targetSystemId: scenario.targetSystemId ?? null,
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

    this.throttlingFormGroup.patchValue({
      responseBytesPerSecond: scenario.responseBytesPerSecond ?? '',
    }, {emitEvent: false});

    const headersArray = this.headerFormGroup.get('headers') as FormArray;
    headersArray.clear();
    if (scenario.headers && scenario.headers.length > 0) {
      scenario.headers.forEach(header => {
        headersArray.push(this.fb.group({
          id: [header.id],
          headerName: [header.headerName || '', Validators.required],
          headerValue: [header.headerValue || '', Validators.required],
          headerReplaceValue: [header.headerReplaceValue || ''],
        }));
      });
    }
  }

  isFormsValid(): boolean {
    return this.basicInfoFormGroup.valid &&
      this.networkFormGroup.valid &&
      this.headerFormGroup.valid &&
      this.throttlingFormGroup.valid &&
      this.statusCodeFormGroup.valid;
  }

  onSubmit(): void {
    if (this.basicInfoFormGroup.valid && this.networkFormGroup.valid && this.headerFormGroup.valid) {
      const basic = this.basicInfoFormGroup.value;
      const network = this.networkFormGroup.value;
      const statusCode = this.statusCodeFormGroup.value;
      const headers = this.headerFormGroup.value.headers || [];
      const throttling = this.throttlingFormGroup.value;

      const scenario: Scenario = {
        id: this.scenario?.id,
        ...basic,
        ...network,
        ...statusCode,
        headers,
        latencyMs: network.latencyMs !== '' && network.latencyMs != null ? Number(network.latencyMs) : undefined,
        timeoutMs: network.timeoutMs !== '' && network.timeoutMs != null ? Number(network.timeoutMs) : undefined,
        responseBytesPerSecond: throttling.responseBytesPerSecond !== '' && throttling.responseBytesPerSecond != null ? Number(throttling.responseBytesPerSecond) : undefined,
      };

      this.formSubmit.emit(scenario);
    } else {
      this.basicInfoFormGroup.markAllAsTouched();
      this.networkFormGroup.markAllAsTouched();
      this.throttlingFormGroup.markAllAsTouched();
    }
  }

  getFormErrors(): string[] {
    const errors: string[] = [];
    this.checkForErrors('Basic scenario information', this.basicInfoFormGroup, errors);
    this.checkForErrors('Network simulation', this.networkFormGroup, errors);
    this.checkForErrors('Status Code', this.statusCodeFormGroup, errors);
    this.checkForErrors('Headers', this.headerFormGroup, errors);
    this.checkForErrors('Throttling', this.throttlingFormGroup, errors);
    return errors;
  }

  public getErrorMessage(fieldName: string, errorKey: string, errorValue: any): string {
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
      'headers': 'Headers',
      'headerName': 'Header Name',
      'headerValue': 'Header Value',
      'headerReplaceValue': 'Replace existing header',
      'responseBytesPerSecond': 'Response Bytes Per Second',
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
