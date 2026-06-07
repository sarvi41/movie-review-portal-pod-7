export interface LoginRequest {
  email: string;
  password: string;
}

export interface UserProfile {
  id: string;
  email: string;
  name: string;
  roles: string[];
  createdAt?: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
  user: UserProfile;
}
