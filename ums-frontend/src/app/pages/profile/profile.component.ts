import {
  Component, inject, signal, OnInit, computed
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule, FormBuilder, Validators,
  AbstractControl, ValidationErrors, FormGroup
} from '@angular/forms';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { UserResponse } from '../../models/user.model';

// ── Validators ─────────────────────────────────────────────────────────────
function newDifferentFromCurrent(control: AbstractControl): ValidationErrors | null {
  const current = control.get('currentPassword')?.value;
  const next    = control.get('newPassword')?.value;
  return current && next && current === next ? { sameasCurrent: true } : null;
}

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const next    = control.get('newPassword')?.value;
  const confirm = control.get('confirmPassword')?.value;
  return next && confirm && next !== confirm ? { mismatch: true } : null;
}

export type Tab = 'details' | 'password';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SidebarComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  private fb          = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);

  // ── Auth state ─────────────────────────────────────────
  readonly username = this.authService.username;

  // ── Data ───────────────────────────────────────────────
  user        = signal<UserResponse | null>(null);
  loading     = signal(true);
  activeTab   = signal<Tab>('details');

  // ── Profile edit state ─────────────────────────────────
  editMode         = signal(false);
  savingProfile    = signal(false);
  profileSuccessAnim = signal(false);

  // ── Password state ─────────────────────────────────────
  submittingPw  = signal(false);
  pwSuccessAnim = signal(false);
  showCurrent   = signal(false);
  showNew       = signal(false);
  showConfirm   = signal(false);

  // ── Derived ────────────────────────────────────────────
  initials = computed(() => {
    const u = this.user();
    if (!u) return '?';
    const first = u.firstName?.[0] ?? '';
    const last  = u.lastName?.[0]  ?? '';
    return (first + last).toUpperCase() || u.username[0].toUpperCase();
  });

  fullName = computed(() => {
    const u = this.user();
    if (!u) return '';
    return [u.firstName, u.lastName].filter(Boolean).join(' ');
  });

  rolePretty = computed(() => {
    const u = this.user();
    if (!u) return '';
    const map: Record<string, string> = {
      ROLE_ADMIN:     'Administrator',
      ROLE_MODERATOR: 'Moderator',
      ROLE_USER:      'User',
    };
    return map[u.role] ?? u.role;
  });

  roleColor = computed(() => {
    const u = this.user();
    if (!u) return '#64748b';
    const map: Record<string, string> = {
      ROLE_ADMIN:     '#f59e0b',
      ROLE_MODERATOR: '#ec4899',
      ROLE_USER:      '#6366f1',
    };
    return map[u.role] ?? '#64748b';
  });

  // ── Forms ──────────────────────────────────────────────
  profileForm = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName:  ['', [Validators.required]],
    email:     ['', [Validators.required, Validators.email]],
  });

  passwordForm: FormGroup = this.fb.group(
    {
      currentPassword: ['', [Validators.required]],
      newPassword:     ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: [newDifferentFromCurrent, passwordsMatch] }
  );

  // ── Lifecycle ──────────────────────────────────────────
  ngOnInit(): void {
    const uname = this.username();
    if (!uname) { this.loading.set(false); return; }

    this.userService.searchUsers(uname).subscribe({
      next: res => {
        if (res.success && res.data?.length) {
          const u = res.data[0];
          this.user.set(u);
          this.profileForm.patchValue({
            firstName: u.firstName,
            lastName:  u.lastName,
            email:     u.email,
          });
        }
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  // ── Helpers ────────────────────────────────────────────
  get pf() { return this.profileForm.controls; }
  get wf() { return this.passwordForm.controls; }

  setTab(tab: Tab): void { this.activeTab.set(tab); }

  enterEdit(): void  { this.editMode.set(true); }
  cancelEdit(): void {
    const u = this.user();
    if (u) {
      this.profileForm.patchValue({ firstName: u.firstName, lastName: u.lastName, email: u.email });
    }
    this.profileForm.markAsPristine();
    this.editMode.set(false);
  }

  toggleShow(field: 'current' | 'new' | 'confirm'): void {
    if (field === 'current') this.showCurrent.update(v => !v);
    if (field === 'new')     this.showNew.update(v => !v);
    if (field === 'confirm') this.showConfirm.update(v => !v);
  }

  getPasswordStrength(pw: string): { level: number; label: string; color: string } {
    if (!pw) return { level: 0, label: '', color: '' };
    let score = 0;
    if (pw.length >= 8)           score++;
    if (/[A-Z]/.test(pw))         score++;
    if (/[0-9]/.test(pw))         score++;
    if (/[^A-Za-z0-9]/.test(pw))  score++;
    if (score <= 1) return { level: 1, label: 'Weak',   color: '#ef4444' };
    if (score === 2) return { level: 2, label: 'Fair',   color: '#f59e0b' };
    if (score === 3) return { level: 3, label: 'Good',   color: '#6366f1' };
    return              { level: 4, label: 'Strong', color: '#10b981' };
  }

  // ── Save profile ───────────────────────────────────────
  saveProfile(): void {
    if (this.profileForm.invalid || !this.user()) return;
    this.savingProfile.set(true);

    const u = this.user()!;
    const payload = {
      firstName: this.pf['firstName'].value!,
      lastName:  this.pf['lastName'].value!,
      email:     this.pf['email'].value!,
    };

    this.userService.updateProfile(u.id, payload).subscribe({
      next: res => {
        this.savingProfile.set(false);
        if (res.success && res.data) {
          this.user.set(res.data);
          this.profileForm.patchValue({
            firstName: res.data.firstName,
            lastName:  res.data.lastName,
            email:     res.data.email,
          });
          this.profileForm.markAsPristine();
        }
        this.editMode.set(false);
        this.profileSuccessAnim.set(true);
        this.toastService.success('Profile updated successfully!');
        setTimeout(() => this.profileSuccessAnim.set(false), 3000);
      },
      error: err => {
        this.savingProfile.set(false);
        this.toastService.error(err?.error?.message ?? 'Failed to update profile.');
      }
    });
  }

  // ── Change password ────────────────────────────────────
  changePassword(): void {
    if (this.passwordForm.invalid || !this.user()) return;
    this.submittingPw.set(true);

    this.userService.changePassword(this.user()!.id, {
      currentPassword: this.wf['currentPassword'].value!,
      newPassword:     this.wf['newPassword'].value!,
    }).subscribe({
      next: () => {
        this.submittingPw.set(false);
        this.pwSuccessAnim.set(true);
        this.toastService.success('Password changed successfully!');
        this.passwordForm.reset();
        setTimeout(() => this.pwSuccessAnim.set(false), 3000);
      },
      error: err => {
        this.submittingPw.set(false);
        this.toastService.error(err?.error?.message ?? 'Failed to change password.');
      }
    });
  }
}
