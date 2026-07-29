import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: number;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<Toast[]>([]);
  private nextId = 0;

  show(type: Toast['type'], message: string, duration = 4000): void {
    const toast: Toast = { id: this.nextId++, type, message };
    this.toasts.update(t => [...t, toast]);
    setTimeout(() => this.remove(toast.id), duration);
  }

  success(message: string): void { this.show('success', message); }
  error(message: string): void { this.show('error', message, 6000); }
  warning(message: string): void { this.show('warning', message); }
  info(message: string): void { this.show('info', message); }

  remove(id: number): void {
    this.toasts.update(t => t.filter(toast => toast.id !== id));
  }
}
