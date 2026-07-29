import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  username = '';
  password = '';
  loading = signal(false);
  showPassword = signal(false);
  errorMessage = signal('');
  currentYear = new Date().getFullYear();

  togglePassword(): void {
    this.showPassword.update(v => !v);
  }

  onSubmit(): void {
    if (!this.username.trim() || !this.password.trim()) {
      this.errorMessage.set('Please enter username and password.');
      return;
    }
    this.loading.set(true);
    this.errorMessage.set('');

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.toastService.success('Welcome back! Login successful.');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        const msg = err.error?.message || 'Invalid username or password.';
        this.errorMessage.set(msg);
        this.loading.set(false);
      }
    });
  }
}
