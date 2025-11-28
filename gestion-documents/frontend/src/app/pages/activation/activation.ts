import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import Swal from 'sweetalert2';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../auth/services/auth.service';

@Component({
  selector: 'app-activation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './activation.html',
  styleUrls: ['./activation.css'],
})
export class Activation implements OnInit {

  message = '';
  success = false;
  loading = true;

  constructor(private route: ActivatedRoute, private authService: AuthService, private router: Router)
  {}

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (token) {
      this.activateAccount(token);
    } else {
      this.handleError('Lien d’activation invalide.');
    }
  }

  private activateAccount(token: string) {
    this.authService.activation(token).subscribe({
      next: (response: any) => {
        this.success = true;
        this.message = response.message || 'Votre compte a été activé avec succès ! 🎉';
        this.loading = false;

        Swal.fire('Activation réussie ✅', this.message, 'success');
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.handleError(err.error?.message || 'Lien invalide ou expiré ❌');
      }
    });
  }

  private handleError(errorMessage: string) {
    this.success = false;
    this.message = errorMessage;
    this.loading = false;
    Swal.fire('Erreur', this.message, 'error');
  }
}
