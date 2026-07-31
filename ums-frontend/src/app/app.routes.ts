import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    canActivate: [loginGuard],
    loadComponent: () =>
      import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'users',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/users/user-list/user-list.component').then(m => m.UserListComponent)
  },
  {
    path: 'users/new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/users/user-form/user-form.component').then(m => m.UserFormComponent)
  },
  {
    path: 'users/edit/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/users/user-form/user-form.component').then(m => m.UserFormComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'audit-logs',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/audit-log/audit-log.component').then(m => m.AuditLogComponent)
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
