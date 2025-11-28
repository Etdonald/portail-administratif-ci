import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  credentials = { email: '', motDePasse: '' };
  loading = false;
  voirPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdRef: ChangeDetectorRef  // ← Ajouté
  ) {}

  onLogin() {
    if (!this.credentials.email || !this.credentials.motDePasse) {
      Swal.fire('Erreur', 'Veuillez remplir tous les champs', 'error');
      return;
    }

    this.loading = true;

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        this.loading = false;
        this.cdRef.detectChanges();  // ← Forcer la détection

        this.authService.saveToken(response.token);
        Swal.fire('Connexion réussie ✅', '', 'success');
        this.router.navigate(['/acceuil']);
      },
      error: (error) => {
        this.loading = false;
        this.cdRef.detectChanges();  // ← Forcer la détection

        Swal.fire('Erreur', 'Email ou mot de passe incorrect', 'error');
      }
    });
  }

  changeVisibilitePassword(): void {
    this.voirPassword = !this.voirPassword;
  }
}
