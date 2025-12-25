import {Component, EventEmitter, inject, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {MatCard} from '@angular/material/card';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TargetSystemService} from '../../services/target-system.service';
import {TargetSystem} from '../../models/target-system';
import {materialImports} from '../../shared/material-imports';
import {MatList, MatListItem} from '@angular/material/list';
import {DeleteTargetSystemDialog} from './dialog-delete-target-system';
import {MatDialog} from '@angular/material/dialog';

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
export class TargetSystemForm implements OnInit {
  readonly dialog = inject(MatDialog);
  @Input() targetSystem?: TargetSystem;
  @Input() submitButtonText: string = 'Submit';
  @Output() formSubmit = new EventEmitter<TargetSystem>();
  @Output() deleteTargetSystemSubmit = new EventEmitter<TargetSystem>();

  targetSystemForm: FormGroup;
  targetSystemService = inject(TargetSystemService);

  constructor(private fb: FormBuilder) {
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

      // Handle FormArray
      if (control instanceof FormArray) {
        control.controls.forEach((formGroup, index) => {
          if (formGroup.errors) {
            Object.keys(formGroup.errors).forEach(errorKey => {
              const message = this.getErrorMessage('', errorKey, formGroup.errors![errorKey]);
              errors.push(`${groupName} [${index + 1}]: ${message}`);
            });
          }

          // Check individual controls within the FormGroup
          if (formGroup instanceof FormGroup) {
            Object.keys(formGroup.controls).forEach(fieldKey => {
              const fieldControl = formGroup.get(fieldKey);
              if (fieldControl?.errors) {
                Object.keys(fieldControl.errors).forEach(errorKey => {
                  const message = this.getErrorMessage(fieldKey, errorKey, fieldControl.errors![errorKey]);
                  errors.push(`${groupName} [${index + 1}]: ${message}`);
                });
              }
            });
          }
        });
      } else if (control?.errors) {
        // Regular control
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
