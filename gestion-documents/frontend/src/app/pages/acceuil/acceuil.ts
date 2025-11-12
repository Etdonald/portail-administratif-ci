import {Component, OnInit} from '@angular/core';
import Swal from 'sweetalert2';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../auth/services/auth.service';

@Component({
  selector: 'app-acceuil',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './acceuil.html',
  styleUrls: ['./acceuil.css']

})
export class Acceuil implements OnInit {
  userRole: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    } else {
      this.userRole = this.authService.getUserRole();
    }
  }

  logout(): void {
    this.authService.logout();
    Swal.fire('Déconnexion réussie', '', 'success');
    this.router.navigate(['/login']);
  }

}
