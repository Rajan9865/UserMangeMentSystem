import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { UserResponse, Role } from '../../../models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);

  username = this.authService.username;

  users = signal<UserResponse[]>([]);
  loading = signal(true);
  searchQuery = '';
  searching = signal(false);

  // Pagination
  currentPage = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);
  pageSize = 10;

  // Delete confirm
  deleteTargetId = signal<number | null>(null);
  deleting = signal(false);

  // Role update
  roleUpdateUserId = signal<number | null>(null);
  roles: Role[] = ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_MODERATOR'];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.userService.getAllUsers(this.currentPage(), this.pageSize).subscribe({
      next: res => {
        if (res.success && res.data) {
          this.users.set(res.data.content);
          this.totalPages.set(res.data.totalPages);
          this.totalElements.set(res.data.totalElements);
        }
        this.loading.set(false);
      },
      error: () => {
        this.toastService.error('Failed to load users.');
        this.loading.set(false);
      }
    });
  }

  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.loadUsers();
      return;
    }
    this.searching.set(true);
    this.userService.searchUsers(this.searchQuery).subscribe({
      next: res => {
        if (res.success && res.data) {
          this.users.set(res.data);
        }
        this.searching.set(false);
      },
      error: () => {
        this.toastService.error('Search failed.');
        this.searching.set(false);
      }
    });
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.loadUsers();
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages()) return;
    this.currentPage.set(page);
    this.loadUsers();
  }

  confirmDelete(id: number): void {
    this.deleteTargetId.set(id);
  }

  cancelDelete(): void {
    this.deleteTargetId.set(null);
  }

  deleteUser(): void {
    const id = this.deleteTargetId();
    if (!id) return;
    this.deleting.set(true);
    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.toastService.success('User deleted successfully.');
        this.deleteTargetId.set(null);
        this.deleting.set(false);
        this.loadUsers();
      },
      error: err => {
        this.toastService.error(err.error?.message || 'Failed to delete user.');
        this.deleting.set(false);
      }
    });
  }

  updateRole(user: UserResponse, role: Role): void {
    this.userService.updateUserRole(user.id, role).subscribe({
      next: res => {
        if (res.success) {
          this.toastService.success(`Role updated to ${this.formatRole(role)}`);
          this.users.update(list => list.map(u => u.id === user.id ? { ...u, role } : u));
        }
        this.roleUpdateUserId.set(null);
      },
      error: err => {
        this.toastService.error(err.error?.message || 'Failed to update role.');
        this.roleUpdateUserId.set(null);
      }
    });
  }

  getRoleBadgeClass(role: string): string {
    const map: Record<string, string> = { ROLE_ADMIN: 'badge-admin', ROLE_USER: 'badge-user', ROLE_MODERATOR: 'badge-mod' };
    return map[role] ?? 'badge-user';
  }

  formatRole(role: string): string {
    return role.replace('ROLE_', '');
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages() }, (_, i) => i);
  }

  logout(): void { this.authService.logout(); }
}
