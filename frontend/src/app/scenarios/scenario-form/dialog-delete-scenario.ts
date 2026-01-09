import {Component, inject, model} from '@angular/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {Scenario} from '../../models/scenario';
import {MatIcon} from '@angular/material/icon';

export interface DialogData {
  scenario: Scenario;
}

@Component({
  selector: 'dialog-delete-scenario',
  templateUrl: 'dialog-delete-scenario.html',
  styleUrls: ['dialog-delete-scenario.scss'],
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatIcon,
  ],
  standalone: true
})
export class DeleteScenarioDialog {
  readonly dialogRef = inject(MatDialogRef<DeleteScenarioDialog>);
  readonly data = inject<DialogData>(MAT_DIALOG_DATA);
  readonly scenario = model(this.data.scenario);

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }

}
