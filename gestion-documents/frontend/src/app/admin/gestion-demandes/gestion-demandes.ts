import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DemandeService } from '../../auth/services/demande.service';
import { Demande } from '../../shared/models/demande.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gestion-demandes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-demandes.html',
  styleUrls: ['./gestion-demandes.css']
})
export class GestionDemandes implements OnInit {
  demandes: Demande[] = [];
  demandesFiltrees: Demande[] = [];
  demandeSelectionnee: Demande | null = null;
  isLoading = false;

  // Filtres
  filtreStatut: string = 'TOUS';
  recherche: string = '';

  // Statistiques
  stats = {
    total: 0,
    enAttente: 0,
    approuvees: 0,
    rejetees: 0
  };

  statuts = [
    { value: 'TOUS', label: 'Tous les statuts' },
    { value: 'EN_ATTENTE', label: 'En attente' },
    { value: 'APPROUVEE', label: 'Approuvées' },
    { value: 'REJETEE', label: 'Rejetées' }
  ];

  constructor(private demandeService: DemandeService, private cdRef: ChangeDetectorRef) {}

  ngOnInit() {
    this.chargerDemandes();
  }

  chargerDemandes() {
    this.isLoading = true;
    this.demandeService.listerToutesDemandes().subscribe({
      next: (demandes) => {
        this.demandes = demandes;
        this.appliquerFiltres();
        this.calculerStats();
        this.isLoading = false;
        this.cdRef.detectChanges();
      },
      error: (error) => {
        console.error('Erreur chargement demandes:', error);
        this.isLoading = false;
        this.cdRef.detectChanges();
        Swal.fire('Erreur', 'Impossible de charger les demandes', 'error');
      }
    });
  }

  appliquerFiltres() {
    this.demandesFiltrees = this.demandes.filter(demande => {
      // Filtre par statut
      const filtreStatut = this.filtreStatut === 'TOUS' || demande.statut === this.filtreStatut;

      // Filtre par recherche
      const filtreRecherche = this.recherche === '' ||
        demande.nom?.toLowerCase().includes(this.recherche.toLowerCase()) ||
        demande.prenoms?.toLowerCase().includes(this.recherche.toLowerCase()) ||
        demande.email?.toLowerCase().includes(this.recherche.toLowerCase());

      return filtreStatut && filtreRecherche;
    });
  }

  calculerStats() {
    this.stats = {
      total: this.demandes.length,
      enAttente: this.demandes.filter(d => d.statut === 'EN_ATTENTE').length,
      approuvees: this.demandes.filter(d => d.statut === 'APPROUVEE').length,
      rejetees: this.demandes.filter(d => d.statut === 'REJETEE').length
    };
  }

  onFiltreChange() {
    this.appliquerFiltres();
  }

  onRechercheChange() {
    this.appliquerFiltres();
  }

  selectionnerDemande(demande: Demande) {
    this.demandeSelectionnee = demande;
  }

  validerDemande(demande: Demande) {
    Swal.fire({
      title: 'Valider cette demande ?',
      text: `Voulez-vous approuver la demande de ${demande.prenoms} ${demande.nom} ?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#198754',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Oui, valider',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        this.demandeService.validerDemande(demande.id!).subscribe({
          next: (demandeValidee) => {
            // Mettre à jour la demande dans la liste
            const index = this.demandes.findIndex(d => d.id === demandeValidee.id);
            if (index !== -1) {
              this.demandes[index] = demandeValidee;
            }
            this.appliquerFiltres();
            this.calculerStats();

            Swal.fire('Validée !', 'La demande a été approuvée avec succès', 'success');
          },
          error: (error) => {
            console.error('Erreur validation:', error);
            Swal.fire('Erreur', 'Impossible de valider la demande', 'error');
          }
        });
      }
    });
  }

  rejeterDemande(demande: Demande) {
    Swal.fire({
      title: 'Motif du rejet',
      input: 'textarea',
      inputLabel: 'Pourquoi rejetez-vous cette demande ?',
      inputPlaceholder: 'Entrez le motif du rejet...',
      inputAttributes: {
        'aria-label': 'Entrez le motif du rejet'
      },
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Rejeter',
      cancelButtonText: 'Annuler',
      inputValidator: (value) => {
        if (!value) {
          return 'Veuillez saisir un motif de rejet';
        }
        return null;
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.demandeService.rejeterDemande(demande.id!, result.value).subscribe({
          next: (demandeRejetee) => {
            // Mettre à jour la demande dans la liste
            const index = this.demandes.findIndex(d => d.id === demandeRejetee.id);
            if (index !== -1) {
              this.demandes[index] = demandeRejetee;
            }
            this.appliquerFiltres();
            this.calculerStats();

            Swal.fire('Rejetée !', 'La demande a été rejetée', 'success');
          },
          error: (error) => {
            console.error('Erreur rejet:', error);
            Swal.fire('Erreur', 'Impossible de rejeter la demande', 'error');
          }
        });
      }
    });
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

  getIconeType(type: string | undefined): string {
    switch (type) {
      case 'CNI': return 'fa-id-card';
      case 'EXTRAIT': return 'fa-certificate';
      case 'PERMIS': return 'fa-car';
      default: return 'fa-file';
    }
  }
}
