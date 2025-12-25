import {AfterViewInit, Component, inject, ViewChild} from '@angular/core';
import {TargetSystemService} from '../../services/target-system.service';
import {TargetSystem} from '../../models/target-system';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {materialImports} from '../../shared/material-imports';
import {Router, RouterLink} from '@angular/router';
import {MatSort, MatSortHeader, Sort} from '@angular/material/sort';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatPaginator} from '@angular/material/paginator';

@Component({
  selector: 'app-target-system-manager',
  templateUrl: './target-system-manager.html',
  styleUrls: ['./target-system-manager.scss'],
  imports: [
    MatTable,
    MatHeaderCell,
    MatCell,
    MatColumnDef,
    materialImports,
    MatHeaderRow,
    MatRow,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    RouterLink,
    MatSort,
    MatSortHeader
  ],
})
export class TargetSystemManagerComponent implements AfterViewInit {
  readonly snackBar = inject(MatSnackBar);
  readonly router = inject(Router);

  displayedColumns: string[] = ['id', 'systemName', 'targetBaseUrl', 'timeoutMs', 'followRedirect'];
  targetSystemService = inject(TargetSystemService);
  // targetSystems: TargetSystem[] = [];
  dataSource = new MatTableDataSource<TargetSystem>([]);
  selectedTargetSystem: TargetSystem | null = null;

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
    this.dataSource.sortingDataAccessor = (item: TargetSystem, property: string) => {
      if (property === 'id') {
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
    this.loadTargetSystems();
  }

  loadTargetSystems(): void {
    this.targetSystemService.getAllTargetSystems().subscribe({
      next: (targetSystems: TargetSystem[]) => {
        console.log("Got target systems: ", targetSystems.length);
        this.dataSource.data = targetSystems;
      },
      error: (err) => {
        console.error('Failed to get target systems', err);
        this.snackBar.open('Failed to get target systems', 'Close', { duration: 5000 });
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

  onRowClick(targetSystem: TargetSystem): void {
    console.log("Row clicked: ", targetSystem);
    this.router.navigate(['/update-target-system', targetSystem.id]);
  }

}
