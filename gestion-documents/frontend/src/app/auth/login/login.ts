import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  credentials = { email: '', motDePasse: '' };
  loading = false;
  voirPassword: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.loading = true;
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token);
        Swal.fire('Connexion réussie ✅', '', 'success');
        this.router.navigate(['/acceuil']);
        this.loading = false;
      },
      error: () => {
        Swal.fire('Erreur', 'Email ou mot de passe incorrect', 'error');
        this.loading = false;
      }
    });
  }

  changeVisibilitePassword(): void {
    this.voirPassword =!this.voirPassword;
  }
}
