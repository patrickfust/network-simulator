import {Component, EventEmitter, inject, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {MatCard} from '@angular/material/card';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TargetSystem} from '../../models/target-system';
import {materialImports} from '../../shared/material-imports';
import {MatList, MatListItem} from '@angular/material/list';
import {DeleteTargetSystemDialog} from './dialog-delete-target-system';
import {MatDialog} from '@angular/material/dialog';
import {NetworkSimulatorForm} from '../../shared/forms/network-simulator-form';

@Component({
  selector: 'app-target-system-form',
  imports: [
    MatButton,
    MatCard,
    ReactiveFormsModule,
    materialImports,
    MatList,
    MatListItem
  ],
  templateUrl: './target-system-form.html',
  styleUrl: './target-system-form.scss',
})
export class TargetSystemForm extends NetworkSimulatorForm implements OnInit {
  readonly dialog = inject(MatDialog);
  @Input() targetSystem?: TargetSystem;
  @Input() submitButtonText: string = 'Submit';
  @Output() formSubmit = new EventEmitter<TargetSystem>();
  @Output() deleteTargetSystemSubmit = new EventEmitter<TargetSystem>();

  targetSystemForm: FormGroup;

  constructor(private fb: FormBuilder) {
    super();
    this.targetSystemForm = this.fb.group({
      systemName: ['', [Validators.required]],
      targetBaseUrl: ['', [Validators.required, Validators.pattern('https?://.+')]],
      timeoutMs: [0, [Validators.required, Validators.min(1)]],
      followRedirect: [true],
    });
  }

  ngOnInit(): void {
    if (this.targetSystem) {
      this.populateForm(this.targetSystem);
    } else {
      // If creating a new target system (no @Input() scenario), set default timeout to 10000 ms
      this.targetSystemForm.patchValue({timeoutMs: '10000'}, {emitEvent: false});
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['targetSystem'] && changes['targetSystem'].currentValue && this.targetSystemForm) {
      this.populateForm(changes['targetSystem'].currentValue);
    }
  }

  private populateForm(targetSystem: TargetSystem): void {
    // Add null checks before patching
    if (!this.targetSystemForm) {
      return;
    }

    this.targetSystemForm.patchValue({
      targetBaseUrl: targetSystem.targetBaseUrl || '',
      timeoutMs: targetSystem.timeoutMs ?? 0,
      systemName: targetSystem.systemName || '',
      followRedirect: targetSystem.followRedirect ?? true,
    }, {emitEvent: false});

  }

  isFormsValid(): boolean {
    return this.targetSystemForm.valid;
  }

  onSubmit(): void {
    if (this.targetSystemForm.valid) {
      const target = this.targetSystemForm.value;

      const targetSystem: TargetSystem = {
        id: this.targetSystem?.id,
        ...target,
        timeoutMs: target.timeoutMs !== '' && target.timeoutMs != null ? Number(target.timeoutMs) : undefined,
      };

      this.formSubmit.emit(targetSystem);
    } else {
      this.targetSystemForm.markAllAsTouched();
    }
  }

  getFormErrors(): string[] {
    const errors: string[] = [];
    this.checkForErrors('Target System', this.targetSystemForm, errors);
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
        return `${fieldLabel} must be a valid URL`;
      default:
        return `${fieldLabel} has an error: ${errorKey}`;
    }
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      'targetBaseUrl': 'Target Base URL',
      'timeoutMs': 'Timeout (ms)',
      'systemName': 'System Name',
      'followRedirect': 'Follow Redirect',
    };

    return labels[fieldName] || fieldName;
  }

  showDeleteTargetSystemDialog(): void {
    const dialogRef = this.dialog.open(DeleteTargetSystemDialog, {
      data: {targetSystem: this.targetSystem},
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result == true) {
        this.deleteTargetSystemSubmit.emit(this.targetSystem);
      }
    });
  }

}
