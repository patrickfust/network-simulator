// typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ScenarioManager } from './scenario-manager';
import { ScenarioService } from '../../services/scenario.service';
import { Scenario } from '../../models/scenario';
import { of, throwError } from 'rxjs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ScenarioManager', () => {
  let fixture: ComponentFixture<ScenarioManager>;
  let component: ScenarioManager;
  let mockService: any;
  let snackBar: MatSnackBar;

  const scenarios: Scenario[] = [
    {id: 1, name: 'A', description: '', path: '/', timeoutMs: 0, followRedirect: true, statusCode: 200, responseBody: 'abc', latencyMs: 0, enableScenario: false, headers: []},
  ];

  beforeEach(async () => {
    mockService = {
      getAllScenarios: jasmine.createSpy('getAllScenarios').and.returnValue(of(scenarios)),
      updateScenario: jasmine.createSpy('updateScenario').and.returnValue(of({ ...scenarios[0], enableScenario: true })),
    };

    await TestBed.configureTestingModule({
      imports: [
        ScenarioManager,
        MatSnackBarModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatInputModule,
        MatSlideToggleModule,
        RouterTestingModule,
        HttpClientTestingModule,
      ],
      providers: [{ provide: ScenarioService, useValue: mockService }],
    }).compileComponents();

    fixture = TestBed.createComponent(ScenarioManager);
    component = fixture.componentInstance;
    snackBar = TestBed.inject(MatSnackBar);
    spyOn(snackBar, 'open');
    fixture.detectChanges();
  });

  it('should create and load scenarios', () => {
    expect(component).toBeTruthy();
    expect(mockService.getAllScenarios).toHaveBeenCalled();
    expect(component.dataSource.data.length).toBe(1);
  });

  it('applyFilter should set dataSource.filter', () => {
    component.applyFilter({ target: { value: 'a' } } as any);
    expect(component.dataSource.filter).toBe('a');
  });

  it('onToggleScenario success updates data and shows snackbar', () => {
    const s = { ...scenarios[0], enableScenario: false };
    component.dataSource.data = [s];
    mockService.updateScenario.and.returnValue(of({ ...s, enableScenario: true }));
    component.onToggleScenario(s, true);
    expect(mockService.updateScenario).toHaveBeenCalledWith(s);
    expect(component.dataSource.data[0].enableScenario).toBe(true);
    expect(snackBar.open).toHaveBeenCalled();
  });

  it('onToggleScenario error reverts toggle and shows snackbar', () => {
    const s = { ...scenarios[0], enableScenario: false };
    component.dataSource.data = [s];
    mockService.updateScenario.and.returnValue(throwError(() => new Error('fail')));

    // Suppress console.error for this test
    spyOn(console, 'error');

    component.onToggleScenario(s, true);
    expect(mockService.updateScenario).toHaveBeenCalled();
    expect(s.enableScenario).toBe(false); // reverted to !event
    expect(snackBar.open).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalled(); // Optional: verify error was logged
  });
});
