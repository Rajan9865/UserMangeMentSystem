import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { TokenService } from './token.service';
import { LoginRequest, LoginResponse, RefreshTokenRequest } from '../../models/auth.model';
import { ApiResult } from '../../models/api-result.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/auth`;

  private http = inject(HttpClient);
  private tokenService = inject(TokenService);
  private router = inject(Router);

  // Reactive signals for auth state — initialized after inject()
  private _isLoggedIn = signal<boolean>(this.tokenService.isLoggedIn());
  private _username = signal<string | null>(this.tokenService.getUsernameFromToken());

  readonly isLoggedIn = computed(() => this._isLoggedIn());
  readonly username = computed(() => this._username());

  login(request: LoginRequest): Observable<ApiResult<LoginResponse>> {
    return this.http.post<ApiResult<LoginResponse>>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.tokenService.saveTokens(response.data.accessToken, response.data.refreshToken);
          this._isLoggedIn.set(true);
          this._username.set(this.tokenService.getUsernameFromToken());
        }
      })
    );
  }

  refreshToken(): Observable<ApiResult<LoginResponse>> {
    const refreshToken = this.tokenService.getRefreshToken();
    const request: RefreshTokenRequest = { refreshToken: refreshToken! };
    return this.http.post<ApiResult<LoginResponse>>(`${this.apiUrl}/refresh`, request).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.tokenService.saveTokens(response.data.accessToken, response.data.refreshToken);
        }
      })
    );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe({
      complete: () => this.clearAndRedirect(),
      error: () => this.clearAndRedirect()
    });
  }

  private clearAndRedirect(): void {
    this.tokenService.clearTokens();
    this._isLoggedIn.set(false);
    this._username.set(null);
    this.router.navigate(['/login']);
  }
}
