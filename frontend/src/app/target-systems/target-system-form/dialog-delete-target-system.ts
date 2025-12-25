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
import {MatIcon} from '@angular/material/icon';
import {TargetSystem} from '../../models/target-system';

export interface DialogData {
  targetSystem: TargetSystem;
}

@Component({
  selector: 'dialog-delete-targetSystem',
  templateUrl: 'dialog-delete-target-system.html',
  styleUrls: ['dialog-delete-target-system.scss'],
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
})
export class DeleteTargetSystemDialog {
  readonly dialogRef = inject(MatDialogRef<DeleteTargetSystemDialog>);
  readonly data = inject<DialogData>(MAT_DIALOG_DATA);
  readonly targetSystem = model(this.data.targetSystem);

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }

}
