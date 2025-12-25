import {Component, inject, signal} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActivatedRoute, Router} from '@angular/router';
import {TargetSystemForm} from '../target-system-form/target-system-form';
import {TargetSystemService} from '../../services/target-system.service';
import {TargetSystem} from '../../models/target-system';

@Component({
  selector: 'app-update-target-system',
  imports: [
    TargetSystemForm
  ],
  templateUrl: './update-target-system.html',
  styleUrl: './update-target-system.scss',
})
export class UpdateTargetSystem {
  readonly snackBar = inject(MatSnackBar);
  private targetSystemService = inject(TargetSystemService);
  private router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute);

  targetSystem = signal<TargetSystem | undefined>(undefined);

  constructor() {
    const targetSystemId = Number(this.route.snapshot.params['id']);
    this.targetSystemService.getTargetSystemById(targetSystemId).subscribe(targetSystem => {
      console.log('Target System loaded:', targetSystem);
      this.targetSystem.set(targetSystem);
    });
  }

  onFormSubmit(targetSystem: TargetSystem): void {
    console.log('TargetSystem to update:', targetSystem);
    this.targetSystemService.updateTargetSystem(targetSystem).subscribe({
      next: (created) => {
        console.log('TargetSystem updated:', created);
        this.snackBar.open('TargetSystem updated', 'Close', {duration: 3000});
        this.router.navigate(['/target-systems']);
      },
      error: (err: Error) => {
        console.error('Update failed:', err);
        this.snackBar.open(err.message, 'Close', {duration: 5000});
      },
    });
  }

  onDeleteSubmit(targetSystem: TargetSystem): void {
    console.log('TargetSystem to delete:', targetSystem);
    const id = this.targetSystem()?.id;
    if (id !== undefined) {
      this.targetSystemService.deleteTargetSystem(id).subscribe({
        next: () => {
          this.snackBar.open('Target System deleted', 'Close', {duration: 5000});
          this.router.navigate(['/target-systems']);
        },
        error: (err: Error) => {
          console.error('Delete failed:', err);
          this.snackBar.open(err.message, 'Close', {duration: 5000});
        },
      });
    }
  }

}
