import { Component, ChangeDetectorRef } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../auth/services/auth.service';
import {Router, RouterLink} from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-inscription',
  standalone: true,
  imports: [FormsModule, CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './inscription.html',
  styleUrls: ['./inscription.css']
})
export class Inscription {

  user = {
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    confirmationMotDePasse: '',
  };
  loading = false;
  voirPassword: boolean = false;
  voirConfirmePassword: boolean = false;

  constructor(private authService: AuthService, private router: Router, private cdRef: ChangeDetectorRef) {}

  onRegister() {

    if (!this.user.nom || !this.user.prenom || !this.user.email || !this.user.motDePasse || !this.user.confirmationMotDePasse) {
      Swal.fire('Champs requis', 'Veuillez remplir tous les champs', 'warning');
      return;
    }

    if (this.user.motDePasse !== this.user.confirmationMotDePasse) {
      Swal.fire('Erreur', 'Les mots de passe ne correspondent pas', 'error');
      return;
    }


    this.loading = true;

    this.authService.inscription(this.user).subscribe({
      next: () => {
        this.loading = false;
        this.cdRef.detectChanges();
        Swal.fire(
          'Inscription réussie ✅',
          'Un lien de confirmation a été envoyé à votre adresse email',
          'success'
        );
        this.router.navigate(['/login']);
      },
      error: (err: { error: { message: any; }; }) => {
        this.loading = false;
        this.cdRef.detectChanges();
        Swal.fire('Erreur', err.error?.message || 'Une erreur est survenue', 'error');

      }
    });
  }

  changeVisibilitePassword(): void {
    this.voirPassword =!this.voirPassword;
  }
  changeVisibiliteConfirmePassword(): void {
    this.voirConfirmePassword =!this.voirConfirmePassword;
  }

}
