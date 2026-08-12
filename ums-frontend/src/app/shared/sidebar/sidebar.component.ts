import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

/**
 * Reusable sidebar shell shared by Dashboard, UserList, and UserForm pages.
 * Handles brand logo, nav links (with routerLinkActive), logged-in user info,
 * and the logout action — all previously copy-pasted across every page.
 */
@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  private authService = inject(AuthService);

  /** Reactive signal from AuthService — auto-updates when the token changes. */
  readonly username = this.authService.username;

  logout(): void {
    this.authService.logout();
  }
}
