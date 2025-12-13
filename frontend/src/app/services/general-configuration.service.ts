import {Injectable} from '@angular/core';
import {Scenario} from '../models/scenario';
import {HttpClient} from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import {GeneralConfiguration} from '../models/general-configuration';

@Injectable({
  providedIn: 'root',
})
export class GeneralConfigurationService {

  url = 'http://localhost:9898/_/api/v1/general-configuration';

  constructor(private http: HttpClient) {
  }

  getGeneralConfiguration(): Observable<GeneralConfiguration> {
    console.log('getGeneralConfiguration');
    return this.http.get<GeneralConfiguration>(this.url);
  }

  updateGeneralConfiguration(generalConfiguration: GeneralConfiguration): Observable<GeneralConfiguration> {
    console.log('update general configuration', generalConfiguration);
    return this.http.put<GeneralConfiguration>(this.url, generalConfiguration).pipe(
      tap((res) => console.log('update configuration success', res)),
      catchError((err) => {
        console.error('update configuration failed', err);
        // Normalize error message for the component
        const message =
          err?.error?.message || err?.message || 'Failed to update configuration';
        return throwError(() => new Error(message));
      })
    );
  }

}
