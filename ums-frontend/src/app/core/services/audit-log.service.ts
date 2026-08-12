import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from '../../models/audit-log.model';
import { ApiResult, PageResult } from '../../models/api-result.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly apiUrl = `${environment.apiUrl}/api/audit-logs`;

  constructor(private http: HttpClient) {}

  getLogs(
    page = 0,
    size = 20,
    search = '',
    sortDir = 'desc'
  ): Observable<ApiResult<PageResult<AuditLog>>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('search', search)
      .set('sortDir', sortDir);
    return this.http.get<ApiResult<PageResult<AuditLog>>>(this.apiUrl, { params });
  }
}
