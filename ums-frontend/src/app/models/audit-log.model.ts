export type AuditAction =
  | 'USER_CREATED'
  | 'USER_UPDATED'
  | 'USER_DELETED'
  | 'USER_ROLE_CHANGED'
  | 'PASSWORD_CHANGED'
  | 'PROFILE_UPDATED';

export interface AuditLog {
  id: number;
  action: AuditAction;
  targetUserId: number;
  targetUsername: string;
  performedBy: string;
  details: string;
  timestamp: string; // ISO string from backend
}
