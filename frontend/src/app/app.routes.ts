import {Routes} from '@angular/router';
import {ScenarioManager} from './scenario-manager/scenario-manager';
import {AddScenario} from './add-scenario/add-scenario';
import {GeneralConfigurationComponent} from './general-configuration/general-configuration';
import {UpdateScenario} from './update-scenario/update-scenario';

export const routes: Routes = [
  {path: '', redirectTo: '/general-configuration', pathMatch: 'full'},
  {path: 'general-configuration', component: GeneralConfigurationComponent},
  {path: 'scenarios', component: ScenarioManager},
  {path: 'add-scenario', component: AddScenario},
  {path: 'update-scenario/:id', component: UpdateScenario},
  {path: '**', redirectTo: '/general-configuration'}
];
