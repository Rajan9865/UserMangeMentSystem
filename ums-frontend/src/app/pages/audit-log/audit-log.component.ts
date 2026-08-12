import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';
import { AuditLogService } from '../../core/services/audit-log.service';
import { ToastService } from '../../core/services/toast.service';
import { AuditLog, AuditAction } from '../../models/audit-log.model';

interface ActionConfig {
  color: string;
  bg: string;
  border: string;
  label: string;
  verb: string;
}

@Component({
  selector: 'app-audit-log',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.scss']
})
export class AuditLogComponent implements OnInit {
  private auditLogService = inject(AuditLogService);
  private toastService     = inject(ToastService);

  // ── Data signals ───────────────────────────────────────
  logs          = signal<AuditLog[]>([]);
  loading       = signal(true);
  currentPage   = signal(0);
  totalPages    = signal(0);
  totalElements = signal(0);
  searchQuery   = signal('');
  sortDir       = signal<'asc' | 'desc'>('desc');

  // ── Search input (two-way bound via ngModel) ──────────
  searchInput = '';

  // ── Filters ────────────────────────────────────────────
  filters = [
    { key: 'ALL',              label: 'All Activity',  color: '#6366f1' },
    { key: 'USER_CREATED',     label: 'Created',       color: '#10b981' },
    { key: 'USER_UPDATED',     label: 'Updated',       color: '#6366f1' },
    { key: 'USER_DELETED',     label: 'Deleted',       color: '#ef4444' },
    { key: 'USER_ROLE_CHANGED',label: 'Role Changed',  color: '#f59e0b' },
    { key: 'PASSWORD_CHANGED', label: 'Password',      color: '#8b5cf6' },
    { key: 'PROFILE_UPDATED',  label: 'Profile',       color: '#06b6d4' },
  ];
  activeFilter = signal('ALL');

  // ── Stats computed from loaded logs ────────────────────
  stats = computed(() => {
    const l = this.logs();
    return {
      total:      this.totalElements(),
      created:    l.filter(x => x.action === 'USER_CREATED').length,
      deleted:    l.filter(x => x.action === 'USER_DELETED').length,
      roleChange: l.filter(x => x.action === 'USER_ROLE_CHANGED').length,
      passwords:  l.filter(x => x.action === 'PASSWORD_CHANGED').length,
    };
  });

  // ── Lifecycle ──────────────────────────────────────────
  ngOnInit(): void { this.loadLogs(); }

  loadLogs(page = 0): void {
    this.loading.set(true);
    this.auditLogService.getLogs(page, 20, this.searchQuery(), this.sortDir()).subscribe({
      next: res => {
        if (res.success && res.data) {
          this.logs.set(res.data.content);
          this.totalPages.set(res.data.totalPages);
          this.totalElements.set(res.data.totalElements);
          this.currentPage.set(page);
        }
        this.loading.set(false);
      },
      error: () => {
        this.toastService.error('Failed to load activity log.');
        this.loading.set(false);
      }
    });
  }

  onSearch(): void {
    this.searchQuery.set(this.searchInput.trim());
    this.loadLogs(0);
  }

  clearSearch(): void {
    this.searchInput = '';
    this.searchQuery.set('');
    this.loadLogs(0);
  }

  toggleSort(): void {
    this.sortDir.update(d => d === 'desc' ? 'asc' : 'desc');
    this.loadLogs(0);
  }

  refresh(): void { this.loadLogs(this.currentPage()); }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages()) return;
    this.loadLogs(page);
  }

  // ── Action config ──────────────────────────────────────
  getActionConfig(action: string): ActionConfig {
    const map: Record<string, ActionConfig> = {
      USER_CREATED:      { color: '#10b981', bg: 'rgba(16,185,129,0.1)',   border: 'rgba(16,185,129,0.25)',  label: 'Created',      verb: 'was created' },
      USER_UPDATED:      { color: '#6366f1', bg: 'rgba(99,102,241,0.1)',   border: 'rgba(99,102,241,0.25)',  label: 'Updated',      verb: 'was updated' },
      USER_DELETED:      { color: '#ef4444', bg: 'rgba(239,68,68,0.1)',    border: 'rgba(239,68,68,0.25)',   label: 'Deleted',      verb: 'was deleted' },
      USER_ROLE_CHANGED: { color: '#f59e0b', bg: 'rgba(245,158,11,0.1)',   border: 'rgba(245,158,11,0.25)',  label: 'Role Changed', verb: 'role was changed' },
      PASSWORD_CHANGED:  { color: '#8b5cf6', bg: 'rgba(139,92,246,0.1)',   border: 'rgba(139,92,246,0.25)',  label: 'Password',     verb: 'password was changed' },
      PROFILE_UPDATED:   { color: '#06b6d4', bg: 'rgba(6,182,212,0.1)',    border: 'rgba(6,182,212,0.25)',   label: 'Profile',      verb: 'profile was updated' },
    };
    return map[action] ?? { color: '#64748b', bg: 'rgba(100,116,139,0.1)', border: 'rgba(100,116,139,0.25)', label: action, verb: 'was modified' };
  }

  // ── Formatting ─────────────────────────────────────────
  formatTime(ts: string): string {
    if (!ts) return '';
    const d = new Date(ts);
    return d.toLocaleString('en-US', {
      month: 'short', day: 'numeric', year: 'numeric',
      hour: 'numeric', minute: '2-digit', hour12: true
    });
  }

  timeAgo(ts: string): string {
    if (!ts) return '';
    const diff = Date.now() - new Date(ts).getTime();
    const mins  = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days  = Math.floor(diff / 86400000);
    if (mins < 1)   return 'just now';
    if (mins < 60)  return `${mins}m ago`;
    if (hours < 24) return `${hours}h ago`;
    if (days === 1) return 'Yesterday';
    return `${days}d ago`;
  }

  getInitials(username: string): string {
    if (!username) return '?';
    return username.slice(0, 2).toUpperCase();
  }

  // ── Pagination ─────────────────────────────────────────
  get pages(): number[] {
    return Array.from({ length: this.totalPages() }, (_, i) => i);
  }

  get visiblePages(): number[] {
    const total = this.totalPages();
    const cur   = this.currentPage();
    if (total <= 7) return this.pages;
    const start = Math.max(0, Math.min(cur - 2, total - 5));
    return Array.from({ length: Math.min(5, total) }, (_, i) => start + i);
  }

  skeletonRows = Array(6).fill(0);
}
