import {ScenarioService} from '../../services/scenario.service';
import {Scenario} from '../../models/scenario';
import {Component, ViewChild, inject, AfterViewInit} from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatSort, Sort, MatSortModule} from '@angular/material/sort';
import {MatPaginatorModule, MatPaginator} from '@angular/material/paginator';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router, RouterLink} from '@angular/router';
import {materialImports} from '../../shared/material-imports';

@Component({
  selector: 'app-scenario-manager',
  imports: [MatPaginatorModule, MatInputModule, MatSortModule, MatTableModule, MatSlideToggleModule, materialImports, RouterLink],
  templateUrl: './scenario-manager.html',
  styleUrl: './scenario-manager.scss',
})

export class ScenarioManager implements AfterViewInit {
  readonly snackBar = inject(MatSnackBar);
  readonly router = inject(Router);

  displayedColumns: string[] = ['enableScenario', 'id', 'name', 'description', 'path', 'statusCode', 'hasBody', 'hasHeaders', 'timeoutMs', 'latencyMs'];
  scenarioService: ScenarioService = inject(ScenarioService);
  dataSource = new MatTableDataSource<Scenario>([]);

  @ViewChild(MatSort) sort: MatSort | undefined;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    // attach MatSort to the data source so headers can sort the table
    this.dataSource.sort = this.sort;

    // Defer sort initialization to avoid ExpressionChangedAfterItHasBeenCheckedError
    setTimeout(() => {
      if (this.sort) {
        this.sort.active = 'id';
        this.sort.direction = 'asc';
      }
    });

    // ensure numeric sorting for the `id` column
    this.dataSource.sortingDataAccessor = (item: Scenario, property: string) => {
      if (property === 'enableScenario') {
        return item.enableScenario;
      } else if (property === 'id') {
        return Number((item as any)[property]);
      }
      return (item as any)[property];
    };
  }

  announceSortChange(sortState: Sort) {
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  constructor() {
    this.scenarioService.getAllScenarios().subscribe({
      next: (scenarios: Scenario[]) => {
        console.log("Got scenarios: ", scenarios.length);
        this.dataSource.data = scenarios;
      },
        error: (err) => {
        console.error('Failed to get scenarios', err);
        this.snackBar.open('Failed to get scenarios', 'Close', { duration: 3000 });
      }
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  onToggleScenario(scenario: Scenario, event: any): void {
    scenario.enableScenario = event;
    this.scenarioService.updateScenario(scenario).subscribe({
      next: (updatedScenario) => {
        console.log('Scenario updated:', updatedScenario);
        // Update the row in the data source
        const index = this.dataSource.data.findIndex(s => s.id === scenario.id);
        if (index !== -1) {
          this.dataSource.data[index] = updatedScenario;
        }
        this.snackBar.open(`Scenario ${scenario.id} updated`, 'Close', {duration: 3000});
      },
      error: (error) => {
        console.error('Error updating scenario:', error);
        // Revert the toggle on error
        scenario.enableScenario = !event;
        this.snackBar.open(`Scenario ${scenario.id} updating failed - ${error}`, 'Close', {duration: 3000});
      }
    });
  }

  onRowClick(scenario: Scenario): void {
    this.router.navigate(['/update-scenario', scenario.id]);
  }
}
