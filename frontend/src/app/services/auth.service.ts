import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import {
  BehaviorSubject,
  catchError,
  map,
  Observable,
  of,
  throwError,
} from "rxjs";
import { environment } from "../../environments/environment";
import { LoginRequest, LoginResponse, UserProfile } from "../models/auth.model";

@Injectable({ providedIn: "root" })
export class AuthService {
  private readonly currentUserSubject = new BehaviorSubject<UserProfile | null>(
    this.loadProfile(),
  );
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<UserProfile> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, request)
      .pipe(
        map((response) => {
          this.storeToken(response.token);
          this.currentUserSubject.next(response.user);
          return response.user;
        }),
        catchError((error) => this.handleError(error)),
      );
  }

  logout(): void {
    localStorage.removeItem("mrp_token");
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem("mrp_token");
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  refreshProfile(): Observable<UserProfile | null> {
    if (!this.getToken()) {
      this.currentUserSubject.next(null);
      return of(null);
    }
    return this.http.get<UserProfile>(`${environment.apiUrl}/auth/me`).pipe(
      map((profile) => {
        this.currentUserSubject.next(profile);
        return profile;
      }),
      catchError((error) => {
        this.logout();
        return throwError(() => error);
      }),
    );
  }

  private loadProfile(): UserProfile | null {
    const token = this.getToken();
    return token ? { id: "", email: "", name: "", roles: [] } : null;
  }

  private storeToken(token: string): void {
    localStorage.setItem("mrp_token", token);
  }

  private handleError(error: HttpErrorResponse) {
    const message =
      error.error?.message || error.statusText || "Unable to authenticate.";
    return throwError(() => new Error(message));
  }
}
