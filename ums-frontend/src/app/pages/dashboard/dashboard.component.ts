import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { UserResponse } from '../../models/user.model';

interface StatCard {
  label: string;
  value: string | number;
  icon: string;
  color: string;
  subtitle: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  username = this.authService.username;
  loading = signal(true);
  recentUsers = signal<UserResponse[]>([]);

  stats = signal<StatCard[]>([
    { label: 'Total Users', value: '—', icon: 'users', color: '#6366f1', subtitle: 'Registered accounts' },
    { label: 'Admins', value: '—', icon: 'shield', color: '#f59e0b', subtitle: 'Admin users' },
    { label: 'Active Users', value: '—', icon: 'activity', color: '#10b981', subtitle: 'Regular users' },
    { label: 'Moderators', value: '—', icon: 'star', color: '#ec4899', subtitle: 'Moderator users' },
  ]);

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.userService.getAllUsers(0, 5).subscribe({
      next: res => {
        if (res.success && res.data) {
          const all = res.data.content;
          const total = res.data.totalElements;
          const admins = all.filter(u => u.role === 'ROLE_ADMIN').length;
          const users = all.filter(u => u.role === 'ROLE_USER').length;
          const mods = all.filter(u => u.role === 'ROLE_MODERATOR').length;

          this.stats.set([
            { label: 'Total Users', value: total, icon: 'users', color: '#6366f1', subtitle: 'Registered accounts' },
            { label: 'Admins', value: admins, icon: 'shield', color: '#f59e0b', subtitle: 'Admin users' },
            { label: 'Active Users', value: users, icon: 'activity', color: '#10b981', subtitle: 'Regular users' },
            { label: 'Moderators', value: mods, icon: 'star', color: '#ec4899', subtitle: 'Moderator users' },
          ]);
          this.recentUsers.set(all.slice(0, 5));
        }
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  logout(): void {
    this.authService.logout();
  }

  getRoleBadgeClass(role: string): string {
    const map: Record<string, string> = {
      'ROLE_ADMIN': 'badge-admin',
      'ROLE_USER': 'badge-user',
      'ROLE_MODERATOR': 'badge-mod'
    };
    return map[role] ?? 'badge-user';
  }

  formatRole(role: string): string {
    return role.replace('ROLE_', '');
  }

  currentYear = new Date().getFullYear();
}
