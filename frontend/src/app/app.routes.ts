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
  {path: 'target-systems/add', component: AddTargetSystem},
  {path: 'target-systems/:id', component: UpdateTargetSystem},
  {path: 'target-systems', component: TargetSystemManagerComponent},
  {path: 'scenarios/add', component: AddScenario},
  {path: 'scenarios/:id', component: UpdateScenario},
  {path: 'scenarios', component: ScenarioManager},
  {path: '**', redirectTo: '/home'}
];
