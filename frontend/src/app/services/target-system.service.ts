import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {TargetSystem} from '../models/target-system';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TargetSystemService {

  private apiUrl = environment.apiUrl + '/api/v1/target-systems';

  constructor(private http: HttpClient) {}

  getAllTargetSystems(): Observable<TargetSystem[]> {
    return this.http.get<TargetSystem[]>(this.apiUrl);
  }

  getTargetSystemById(id: number): Observable<TargetSystem> {
    return this.http.get<TargetSystem>(`${this.apiUrl}/${id}`);
  }

  getTargetSystemByName(systemName: string): Observable<TargetSystem> {
    return this.http.get<TargetSystem>(`${this.apiUrl}/name/${systemName}`);
  }

  createTargetSystem(targetSystem: TargetSystem): Observable<TargetSystem> {
    return this.http.post<TargetSystem>(this.apiUrl, targetSystem);
  }

  updateTargetSystem(targetSystem: TargetSystem): Observable<TargetSystem> {
    return this.http.put<TargetSystem>(`${this.apiUrl}/${targetSystem.id}`, targetSystem);
  }

  deleteTargetSystem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}
