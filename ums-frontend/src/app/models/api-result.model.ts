export interface ApiResult<T> {
  success: boolean;
  status: number;
  message: string;
  data: T;
  errorCode?: string;
  errors?: { [key: string]: string };
  path?: string;
  timestamp?: string;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ErrorResponse {
  success: boolean;
  status: number;
  errorCode: string;
  message: string;
  path: string;
  timestamp: string;
  errors?: { [key: string]: string };
}
