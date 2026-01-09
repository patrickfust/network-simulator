import {AbstractControl, FormArray, FormGroup, ValidationErrors} from '@angular/forms';

export abstract class NetworkSimulatorForm {

  // Subclasses must implement this to provide error messages.
  public abstract getErrorMessage(fieldName: string, errorKey: string, errorValue: any): string;

  public checkForErrors(groupName: string, formGroup: FormGroup, errors: string[]) {
    if (formGroup.errors) {
      Object.keys(formGroup.errors).forEach(errorKey => {
        const message = this.getErrorMessage('', errorKey, formGroup.errors![errorKey]);
        errors.push(`${groupName}: ${message}`);
      });
    }

    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);

      if (control instanceof FormArray) {
        control.controls.forEach((formGroup, index) => {
          if (formGroup.errors) {
            Object.keys(formGroup.errors).forEach(errorKey => {
              const message = this.getErrorMessage('', errorKey, formGroup.errors![errorKey]);
              errors.push(`${groupName} [${index + 1}]: ${message}`);
            });
          }

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
        Object.keys(control.errors).forEach(errorKey => {
          const message = this.getErrorMessage(key, errorKey, control.errors![errorKey]);
          errors.push(`${groupName}: ${message}`);
        });
      }
    });
  }

}
