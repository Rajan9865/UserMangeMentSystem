import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container">
      @for (toast of toastService.toasts(); track toast.id) {
        <div class="toast toast-{{ toast.type }}" [class.show]="true">
          <span class="toast-icon">
            @switch (toast.type) {
              @case ('success') { ✓ }
              @case ('error') { ✕ }
              @case ('warning') { ⚠ }
              @default { ℹ }
            }
          </span>
          <span class="toast-message">{{ toast.message }}</span>
          <button class="toast-close" (click)="toastService.remove(toast.id)">×</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 1.5rem;
      right: 1.5rem;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      max-width: 380px;
    }
    .toast {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.875rem 1.25rem;
      border-radius: 12px;
      backdrop-filter: blur(12px);
      border: 1px solid rgba(255,255,255,0.1);
      animation: slideIn 0.3s ease;
      box-shadow: 0 8px 32px rgba(0,0,0,0.3);
    }
    @keyframes slideIn {
      from { transform: translateX(120%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
    .toast-success { background: rgba(16, 185, 129, 0.9); color: #fff; }
    .toast-error   { background: rgba(239, 68, 68, 0.9); color: #fff; }
    .toast-warning { background: rgba(245, 158, 11, 0.9); color: #fff; }
    .toast-info    { background: rgba(99, 102, 241, 0.9); color: #fff; }
    .toast-icon    { font-size: 1.1rem; font-weight: 700; flex-shrink: 0; }
    .toast-message { flex: 1; font-size: 0.875rem; font-weight: 500; }
    .toast-close   { background: none; border: none; color: inherit; font-size: 1.2rem; cursor: pointer; opacity: 0.7; padding: 0; flex-shrink: 0; }
    .toast-close:hover { opacity: 1; }
  `]
})
export class ToastContainerComponent {
  toastService = inject(ToastService);
}
