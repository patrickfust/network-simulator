import {ScenarioService} from '../../services/scenario.service';
import {Scenario} from '../../models/scenario';
import {AfterViewInit, Component, inject, ViewChild} from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatSort, MatSortModule, Sort} from '@angular/material/sort';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router, RouterLink} from '@angular/router';
import {materialImports} from '../../shared/material-imports';
import {TargetSystemService} from '../../services/target-system.service';
import {TargetSystem} from '../../models/target-system';

@Component({
  selector: 'app-scenario-manager',
  imports: [MatPaginatorModule, MatInputModule, MatSortModule, MatTableModule, MatSlideToggleModule, materialImports, RouterLink],
  templateUrl: './scenario-manager.html',
  styleUrl: './scenario-manager.scss',
})
export class ScenarioManager implements AfterViewInit {
  readonly snackBar = inject(MatSnackBar);
  readonly router = inject(Router);

  displayedColumns: string[] = ['enableScenario', 'id', 'name', 'description', 'targetSystem', 'path', 'statusCode', 'hasBody', 'hasHeaders', 'timeoutMs', 'latencyMs', 'responseBytesPerSecond'];
  scenarioService: ScenarioService = inject(ScenarioService);
  targetSystemService: TargetSystemService = inject(TargetSystemService);
  dataSource = new MatTableDataSource<Scenario>([]);
  targetSystems?: TargetSystem[];

  @ViewChild(MatSort) sort: MatSort | undefined;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    // attach MatSort to the data source so headers can sort the table
    this.dataSource.sort = this.sort;

    // Defer sort initialization to avoid ExpressionChangedAfterItHasBeenCheckedError
    Promise.resolve().then(() => {
      if (this.sort) {
        this.sort.active = 'id';
        this.sort.direction = 'asc';
      }
    });

    // ensure numeric sorting for the `id` column and map targetSystem to targetSystemId
    this.dataSource.sortingDataAccessor = (item: Scenario, property: string) => {
      if (property === 'enableScenario') {
        return item.enableScenario;
      } else if (property === 'id') {
        return Number((item as any)[property]);
      } else if (property === 'targetSystem') {
        // lookup name and normalize for case-insensitive sorting
        const name = this.getTargetSystemNameById((item as any).targetSystemId);
        return name ? name.toLowerCase() : '';
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
    this.getAllTargetSystems()
  }

  getTargetSystemNameById(id: number): string {
    const targetSystem = this.targetSystems?.find(ts => ts.id === id);
    return targetSystem ? targetSystem.systemName : '';
  }

  private getAllTargetSystems() {
    this.targetSystemService.getAllTargetSystems().subscribe({
      next: (targetSystems) => {
        console.log("Got target systems: ", targetSystems.length);
        this.targetSystems = targetSystems;
        this.getAllScenarios();
      },
      error: (err) => {
        console.error('Failed to get target systems', err);
        this.snackBar.open('Failed to get target systems', 'Close', {duration: 3000});
      }
    });
  }

  private getAllScenarios() {
    this.scenarioService.getAllScenarios().subscribe({
      next: (scenarios: Scenario[]) => {
        console.log("Got scenarios: ", scenarios.length);
        this.dataSource.data = scenarios;
      },
      error: (err) => {
        console.error('Failed to get scenarios', err);
        this.snackBar.open('Failed to get scenarios', 'Close', {duration: 3000});
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
    if (scenario.enableScenario) {
      this.scenarioService.activateScenarioById(scenario.id).subscribe(this.getObserverOrNext(scenario, event));
    } else {
      this.scenarioService.deactivateScenarioById(scenario.id).subscribe(this.getObserverOrNext(scenario, event));
    }
  }

  private getObserverOrNext(scenario: Scenario, event: any) {
    return {
      next: (updatedScenario: Scenario) => {
        console.log('Scenario updated:', updatedScenario);
        // Update the row in the data source and reassign to trigger change detection
        const index = this.dataSource.data.findIndex(s => s.id === scenario.id);
        if (index !== -1) {
          this.dataSource.data[index] = updatedScenario;
          this.dataSource.data = [...this.dataSource.data];
        }
        this.snackBar.open(`Scenario ${scenario.id} updated`, 'Close', {duration: 3000});
      },
      error: (error: any) => {
        console.error('Error updating scenario:', error);
        // Revert the toggle on error
        scenario.enableScenario = !event;
        this.snackBar.open(`Scenario ${scenario.id} updating failed - ${error}`, 'Close', {duration: 3000});
      }
    };
  }

  onRowClick(scenario: Scenario): void {
    this.router.navigate(['/update-scenario', scenario.id]);
  }
}
