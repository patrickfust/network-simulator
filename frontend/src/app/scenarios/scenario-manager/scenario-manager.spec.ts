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
import {TargetSystem} from '../../models/target-system';
import {TargetSystemService} from '../../services/target-system.service';

describe('ScenarioManager', () => {
  let fixture: ComponentFixture<ScenarioManager>;
  let component: ScenarioManager;
  let mockScenarioService: any;
  let mockTargetSystemService: any;
  let snackBar: MatSnackBar;

  const scenarios: Scenario[] = [
    {
      id: 1,
      name: 'A',
      description: '',
      path: '/',
      timeoutMs: 0,
      followRedirect: true,
      statusCode: 200,
      bodyToReturn: 'abc',
      latencyMs: 0,
      enableScenario: false,
      headers: [],
    },
  ];

  const targetSystems: TargetSystem[] = [
    {
      id: 1,
      systemName: 'System name',
      targetBaseUrl: 'http://localhost:8087',
      followRedirect: true,
      timeoutMs: 1000
    }
  ];

  beforeEach(async () => {
    mockScenarioService = {
      getAllScenarios: jasmine.createSpy('getAllScenarios').and.returnValue(of(scenarios)),
      updateScenario: jasmine.createSpy('updateScenario').and.returnValue(of({ ...scenarios[0], enableScenario: true })),
      activateScenarioById: jasmine.createSpy('activateScenarioById').and.returnValue(of({ ...scenarios[0], enableScenario: true })),
      deactivateScenarioById: jasmine.createSpy('deactivateScenarioById').and.returnValue(of({ ...scenarios[0], enableScenario: false })),
    };
    mockTargetSystemService = {
      getAllTargetSystems: jasmine.createSpy('getAllTargetSystems').and.returnValue(of(targetSystems)),
    }

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
      providers: [
        { provide: ScenarioService, useValue: mockScenarioService },
        { provide: TargetSystemService, useValue: mockTargetSystemService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ScenarioManager);
    component = fixture.componentInstance;
    snackBar = TestBed.inject(MatSnackBar);
    spyOn(snackBar, 'open');
    fixture.detectChanges();
  });

  it('should create and load scenarios', () => {
    expect(component).toBeTruthy();
    expect(mockTargetSystemService.getAllTargetSystems).toHaveBeenCalled();
    expect(mockScenarioService.getAllScenarios).toHaveBeenCalled();
    expect(component.dataSource.data.length).toBe(1);
  });

  it('applyFilter should set dataSource.filter', () => {
    component.applyFilter({ target: { value: 'a' } } as any);
    expect(component.dataSource.filter).toBe('a');
  });

  it('onToggleScenario success updates data and shows snackbar', () => {
    const s = { ...scenarios[0], enableScenario: false };
    component.dataSource.data = [s];
    mockScenarioService.activateScenarioById.and.returnValue(of({ ...s, enableScenario: true }));
    component.onToggleScenario(s, true);
    expect(mockScenarioService.activateScenarioById).toHaveBeenCalledWith(1);
    expect(component.dataSource.data[0].enableScenario).toBe(true);
    expect(snackBar.open).toHaveBeenCalled();
  });

  it('onToggleScenario error reverts toggle and shows snackbar', () => {
    const s = { ...scenarios[0], enableScenario: true };
    component.dataSource.data = [s];
    mockScenarioService.activateScenarioById.and.returnValue(throwError(() => new Error('fail')));

    // Suppress console.error for this test
    spyOn(console, 'error');

    component.onToggleScenario(s, true);
    expect(mockScenarioService.activateScenarioById).toHaveBeenCalledWith(1);
    expect(s.enableScenario).toBe(false); // reverted to !event
    expect(snackBar.open).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalled(); // Optional: verify error was logged
  });
});
