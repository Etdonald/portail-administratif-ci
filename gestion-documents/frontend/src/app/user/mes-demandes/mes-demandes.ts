import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DemandeService } from '../../auth/services/demande.service';
import { Demande } from '../../shared/models/demande.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mes-demandes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './mes-demandes.html',
  styleUrls: ['./mes-demandes.css']
})
export class MesDemandes implements OnInit {
  demandes: Demande[] = [];
  demandesFiltrees: Demande[] = [];
  isLoading = false;
  filtreType: string = 'TOUS';

  constructor(private demandeService: DemandeService, private cdRef: ChangeDetectorRef) {}

  ngOnInit() {
    this.chargerMesDemandes();
  }

  chargerMesDemandes() {
    this.isLoading = true;

    this.demandeService.getMesDemandes().subscribe({
      next: (demandes) => {
        this.demandes = demandes;
        // Appliquer le filtre par défaut immédiatement
        this.appliquerFiltre();
        this.isLoading = false;
        this.cdRef.detectChanges();
      },
      error: (error) => {
        console.error('Erreur chargement demandes:', error);
        this.isLoading = false;
        this.cdRef.detectChanges();
        Swal.fire({
          title: 'Erreur',
          text: 'Impossible de charger vos demandes',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }

  // Méthode appelée quand le filtre change
  onFiltreChange() {
    this.appliquerFiltre();
    console.log('Filtre appliqué:', this.filtreType, 'Résultats:', this.demandesFiltrees.length);
  }

  // Nouvelle méthode pour appliquer le filtre
  private appliquerFiltre() {
    if (this.filtreType === 'TOUS') {
      this.demandesFiltrees = [...this.demandes]; // Crée une copie
    } else {
      this.demandesFiltrees = this.demandes.filter(d => d.typeDemande === this.filtreType);
    }
  }

  getBadgeClass(statut: string | undefined): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'bg-warning text-dark';
      case 'APPROUVEE': return 'bg-success text-white';
      case 'REJETEE': return 'bg-danger text-white';
      default: return 'bg-secondary text-white';
    }
  }

  getTypeBadgeClass(type: string | undefined): string {
    switch (type) {
      case 'CNI': return 'bg-primary text-white';
      case 'EXTRAIT': return 'bg-info text-white';
      case 'PERMIS': return 'bg-warning text-dark';
      default: return 'bg-secondary text-white';
    }
  }

  telechargerPdf(demande: Demande) {
    if (!demande.id) return;

    this.demandeService.telechargerCniPdf(demande.id).subscribe({
      next: (blob) => {
        // Création d'un lien temporaire pour téléchargement
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `CNI_${demande.numeroCNI}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Erreur téléchargement CNI:', err);
        Swal.fire({
          title: 'Erreur',
          text: 'Impossible de télécharger la CNI',
          icon: 'error',
          confirmButtonText: 'OK'
        });
      }
    });
  }


}
