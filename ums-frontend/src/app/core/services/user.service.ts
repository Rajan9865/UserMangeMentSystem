import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserRequest, UserResponse, Role } from '../../models/user.model';
import { ApiResult, PageResult } from '../../models/api-result.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/api/users`;

  constructor(private http: HttpClient) {}

  createUser(request: UserRequest): Observable<ApiResult<UserResponse>> {
    return this.http.post<ApiResult<UserResponse>>(this.apiUrl, request);
  }

  getUserById(id: number): Observable<ApiResult<UserResponse>> {
    return this.http.get<ApiResult<UserResponse>>(`${this.apiUrl}/${id}`);
  }

  getAllUsers(page = 0, size = 10, sortBy = 'id', direction = 'asc'): Observable<ApiResult<PageResult<UserResponse>>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);
    return this.http.get<ApiResult<PageResult<UserResponse>>>(this.apiUrl, { params });
  }

  updateUser(id: number, request: UserRequest): Observable<ApiResult<UserResponse>> {
    return this.http.put<ApiResult<UserResponse>>(`${this.apiUrl}/${id}`, request);
  }

  deleteUser(id: number): Observable<ApiResult<null>> {
    return this.http.delete<ApiResult<null>>(`${this.apiUrl}/${id}`);
  }

  searchUsers(username: string): Observable<ApiResult<UserResponse[]>> {
    const params = new HttpParams().set('username', username);
    return this.http.get<ApiResult<UserResponse[]>>(`${this.apiUrl}/search`, { params });
  }

  updateUserRole(id: number, role: Role): Observable<ApiResult<UserResponse>> {
    return this.http.put<ApiResult<UserResponse>>(`${this.apiUrl}/${id}/role`, role);
  }
}
