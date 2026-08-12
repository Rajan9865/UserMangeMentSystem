import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { UserRequest } from '../../../models/user.model';
import { SidebarComponent } from '../../../shared/sidebar/sidebar.component';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SidebarComponent],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  private userService = inject(UserService);
  private toastService = inject(ToastService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  isEditMode = signal(false);
  userId = signal<number | null>(null);
  loading = signal(false);
  loadingUser = signal(false);
  showPassword = signal(false);

  validationErrors = signal<Record<string, string>>({});

  form: UserRequest = {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.userId.set(Number(id));
      this.loadUser(Number(id));
    }
  }

  loadUser(id: number): void {
    this.loadingUser.set(true);
    this.userService.getUserById(id).subscribe({
      next: res => {
        if (res.success && res.data) {
          const u = res.data;
          this.form = {
            username: u.username,
            firstName: u.firstName,
            lastName: u.lastName,
            email: u.email,
            password: ''
          };
        }
        this.loadingUser.set(false);
      },
      error: () => {
        this.toastService.error('Failed to load user.');
        this.loadingUser.set(false);
        this.router.navigate(['/users']);
      }
    });
  }

  onSubmit(): void {
    this.validationErrors.set({});
    this.loading.set(true);

    const request = this.isEditMode()
      ? this.userService.updateUser(this.userId()!, this.form)
      : this.userService.createUser(this.form);

    request.subscribe({
      next: res => {
        if (res.success) {
          this.toastService.success(this.isEditMode() ? 'User updated successfully!' : 'User created successfully!');
          this.router.navigate(['/users']);
        }
        this.loading.set(false);
      },
      error: err => {
        if (err.error?.errors) {
          this.validationErrors.set(err.error.errors);
        }
        this.toastService.error(err.error?.message || 'Something went wrong.');
        this.loading.set(false);
      }
    });
  }

  togglePassword(): void { this.showPassword.update(v => !v); }

  getError(field: string): string { return this.validationErrors()[field] ?? ''; }
}
