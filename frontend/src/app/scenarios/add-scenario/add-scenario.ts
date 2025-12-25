import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Scenario} from '../../models/scenario';
import {ScenarioService} from '../../services/scenario.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {ScenarioForm} from '../scenario-form/scenario-form';

@Component({
  selector: 'app-add-scenario',
  imports: [
    CommonModule,
    ScenarioForm,
  ],
  templateUrl: './add-scenario.html',
  styleUrl: './add-scenario.scss',
})
export class AddScenario {
  readonly snackBar = inject(MatSnackBar);
  private scenarioService = inject(ScenarioService);
  private router = inject(Router);

  onFormSubmit(scenario: Omit<Scenario, 'id'>): void {
    console.log('Scenario to create:', scenario);
    this.scenarioService.createScenario(scenario).subscribe({
      next: (created) => {
        console.log('Scenario created:', created);
        this.snackBar.open('Scenario created', 'Close', {duration: 3000});
        this.router.navigate(['/scenarios']);
      },
      error: (err: Error) => {
        console.error('Create failed:', err);
        this.snackBar.open(err.message, 'Close', {duration: 5000});
      },
    });
  }

}
