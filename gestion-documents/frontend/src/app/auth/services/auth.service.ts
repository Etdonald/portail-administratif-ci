import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:5050/api/auth';
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  inscription(data: any) {
    return this.http.post(`${this.apiUrl}/inscription`, data);
  }

  activation(token: string) {
    return this.http.get(`${this.apiUrl}/activation`, {params: {token} });
  }

  login(credentials: { email: string; motDePasse: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return token != null && !this.jwtHelper.isTokenExpired(token);
  }

  logout(): void {
    localStorage.removeItem('token');
  }


  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log('🔍 Token payload avec rôle:', payload);
      console.log('🎭 Rôle détecté:', payload.role);

      return payload.role || null;
    } catch (e) {
      console.error('Error decoding token:', e);
      return null;
    }
  }

  getUserId(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub || payload.id || null; // selon ce que ton backend met dans le JWT
    } catch (e) {
      return null;
    }
  }

}
