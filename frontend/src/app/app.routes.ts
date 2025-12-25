import {Routes} from '@angular/router';
import {ScenarioManager} from './scenarios/scenario-manager/scenario-manager';
import {AddScenario} from './scenarios/add-scenario/add-scenario';
import {UpdateScenario} from './scenarios/update-scenario/update-scenario';
import {Home} from './home/home';
import {TargetSystemManagerComponent} from './target-systems/target-system-manager/target-system-manager';
import {AddTargetSystem} from './target-systems/add-target-system/add-target-system';
import {UpdateTargetSystem} from './target-systems/update-target-system/update-target-system';

export const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: Home},
  {path: 'target-systems', component: TargetSystemManagerComponent},
  {path: 'add-target-system', component: AddTargetSystem},
  {path: 'update-target-system/:id', component: UpdateTargetSystem},
  {path: 'scenarios', component: ScenarioManager},
  {path: 'add-scenario', component: AddScenario},
  {path: 'update-scenario/:id', component: UpdateScenario},
  {path: '**', redirectTo: '/home'}
];
