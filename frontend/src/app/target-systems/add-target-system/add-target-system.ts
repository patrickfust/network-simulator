import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TargetSystemService} from '../../services/target-system.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {TargetSystem} from '../../models/target-system';
import {TargetSystemForm} from '../target-system-form/target-system-form';

@Component({
  selector: 'app-add-target-system',
  imports: [
    CommonModule,
    TargetSystemForm,
  ],
  templateUrl: './add-target-system.html',
  styleUrl: './add-target-system.scss',
})
export class AddTargetSystem {
  readonly snackBar = inject(MatSnackBar);
  private targetSystemService = inject(TargetSystemService);
  private router = inject(Router);

  onFormSubmit(targetSystem: Omit<TargetSystem, 'id'>): void {
    console.log('Target System to create:', targetSystem);
    this.targetSystemService.createTargetSystem(targetSystem).subscribe({
      next: (created) => {
        console.log('Target System created:', created);
        this.snackBar.open('Target System created', 'Close', {duration: 3000});
        this.router.navigate(['/target-systems']);
      },
      error: (err: Error) => {
        console.error('Create failed:', err);
        this.snackBar.open(err.message, 'Close', {duration: 5000});
      },
    });
  }

}
