import {Injectable} from '@angular/core';
import {Scenario} from '../models/scenario';
import {HttpClient} from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ScenarioService {

  url = 'http://localhost:9898/_/api/v1/scenarios';

  constructor(private http: HttpClient) {
  }

  getAllScenarios(): Observable<Scenario[]> {
    console.log('getAllScenarios');
    return this.http.get<Scenario[]>(this.url);
  }

  createScenario(scenario: Omit<Scenario, 'id'>): Observable<Scenario> {
    console.log('createScenario', scenario);
    return this.http.post<Scenario>(this.url, scenario).pipe(
      tap((res) => console.log('createScenario success', res)),
      catchError((err) => {
        console.error('createScenario failed', err);

        // Normalize error message for the component
        const message =
          err?.error?.message || err?.message || 'Failed to create scenario';
        return throwError(() => new Error(message));
      })
    );
  }

  getScenarioById(id: number): Observable<Scenario> {
    console.log(`getScenarioById: ${id}`);
    const getUrl = `${this.url}/${id}`;
    return this.http.get<Scenario>(getUrl);
  }

  updateScenario(scenario: Scenario): Observable<Scenario> {
    console.log(`Updating Scenario`, scenario);
    const updateUrl = `${this.url}/${scenario.id}`;
    return this.http.put<Scenario>(updateUrl, scenario).pipe(
      tap((res) => console.log('updateScenario success', res)),
      catchError((err) => {
        console.error('updateScenario failed', err);

        // Normalize error message for the component
        const message =
          err?.error?.message || err?.message || 'Failed to update scenario';
        return throwError(() => new Error(message));
      })
    );
  }

  deleteScenarioById(id: number) {
    console.log(`Deleting Scenario ${id}`);
    const deleteUrl = `${this.url}/${id}`;
    return this.http.delete<Scenario>(deleteUrl).pipe(
      tap((res) => console.log('delete success', res)),
      catchError((err) => {
        console.error('Delete scenario failed', err);
        // Normalize error message for the component
        const message =
          err?.error?.message || err?.message || 'Failed to delete scenario';
        return throwError(() => new Error(message));
      })
    );

  }

}
