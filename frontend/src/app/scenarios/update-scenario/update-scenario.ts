import {Component, inject, signal} from '@angular/core';
import {ScenarioForm} from "../scenario-form/scenario-form";
import {MatSnackBar} from '@angular/material/snack-bar';
import {ScenarioService} from '../../services/scenario.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Scenario} from '../../models/scenario';

@Component({
  selector: 'app-update-scenario',
  imports: [
    ScenarioForm
  ],
  templateUrl: './update-scenario.html',
  styleUrl: './update-scenario.scss',
})
export class UpdateScenario {
  readonly snackBar = inject(MatSnackBar);
  private scenarioService = inject(ScenarioService);
  private router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute);

  scenario = signal<Scenario | undefined>(undefined);

  constructor() {
    const scenarioId = Number(this.route.snapshot.params['id']);
    this.scenarioService.getScenarioById(scenarioId).subscribe(scenario => {
      console.log('Scenario loaded:', scenario);
      this.scenario.set(scenario);
    });
  }

  onFormSubmit(scenario: Scenario): void {
    console.log('Scenario to update:', scenario);
    this.scenarioService.updateScenario(scenario).subscribe({
      next: (created) => {
        console.log('Scenario updated:', created);
        this.snackBar.open('Scenario updated', 'Close', {duration: 3000});
        this.router.navigate(['/scenarios']);
      },
      error: (err: Error) => {
        console.error('Update failed:', err);
        this.snackBar.open(err.message, 'Close', {duration: 5000});
      },
    });
  }

  onDeleteSubmit(scenario: Scenario): void {
    console.log('Scenario to delete:', scenario);
    const id = this.scenario()?.id;
    if (id !== undefined) {
      this.scenarioService.deleteScenarioById(id).subscribe({
        next: () => {
          this.snackBar.open('Scenario deleted', 'Close', {duration: 3000});
          this.router.navigate(['/scenarios']);
        },
        error: (err: Error) => {
          console.error('Delete failed:', err);
          this.snackBar.open(err.message, 'Close', {duration: 5000});
        },
      });
    }
  }

}
