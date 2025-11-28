import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { DemandeService } from '../../auth/services/demande.service';

@Component({
  selector: 'app-demande-cni',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, ReactiveFormsModule],
  templateUrl: './demande-cni.html',
  styleUrls: ['./demande-cni.css']
})
export class DemandeCni {
  demandeForm: FormGroup;
  isSubmitting = false;
  photoPreview: string | null = null;
  ancienneCniPreview: string | null = null;

  photoIdentite: File | null = null;
  ancienneCni: File | null = null;

  regions = [
    'Abidjan', 'Yamoussoukro', 'Bouaké', 'Daloa', 'Korhogo', 'San-Pédro',
    'Divo', 'Man', 'Gagnoa', 'Abengourou', 'Grand-Bassam', 'Bondoukou'
  ];

  typeDemandeCni = [
    { value: 'PREMIERE_DEMANDE', label: 'Première demande' },
    { value: 'RENOUVELLEMENT', label: 'Renouvellement' },
    { value: 'DUPLICATA', label: 'Duplicata' }
  ];

  constructor(
    private fb: FormBuilder,
    private demandeService: DemandeService,
    private router: Router
  ) {
    this.demandeForm = this.createForm();
  }

  createForm(): FormGroup {
    return this.fb.group({
      typeDemandeCni: ['PREMIERE_DEMANDE', Validators.required],

      nom: ['', [Validators.required, Validators.minLength(2)]],
      prenoms: ['', [Validators.required, Validators.minLength(2)]],
      profession: ['', Validators.required],
      sexe: ['', Validators.required],
      dateNaissance: ['', Validators.required],
      lieuNaissance: ['', Validators.required],
      nationalite: ['Ivoirienne', Validators.required],
      taille: ['', [Validators.required, Validators.pattern(/^\d(\.\d{1,2})?$/)]],

      adresse: ['', Validators.required],
      ville: ['', Validators.required],
      region: ['', Validators.required],
      telephone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      email: ['', [Validators.required, Validators.email]],


      photoIdentite: [null, Validators.required],
      ancienneCni: [null],

      consentementTraitement: [false, Validators.requiredTrue],
      consentementVerification: [false, Validators.requiredTrue]
    });
  }

  onSubmit() {
    if (this.demandeForm.invalid) {
      this.markFormGroupTouched();
      this.showFormErrors();
      return;
    }

    if (!this.photoIdentite) {
      Swal.fire('Erreur', 'Veuillez sélectionner une photo d\'identité', 'error');
      this.demandeForm.get('photoIdentite')?.markAsTouched();
      return;
    }

    this.isSubmitting = true;

    const formData = new FormData();

    const demandeData = {
      typeDemande: 'CNI',
      typeDemandeCni: this.demandeForm.value.typeDemandeCni,
      nom: this.demandeForm.value.nom,
      prenoms: this.demandeForm.value.prenoms,
      profession: this.demandeForm.value.profession,
      sexe: this.demandeForm.value.sexe,
      dateNaissance: this.demandeForm.value.dateNaissance,
      lieuNaissance: this.demandeForm.value.lieuNaissance,
      nationalite: this.demandeForm.value.nationalite,
      taille: this.demandeForm.value.taille,
      adresse: this.demandeForm.value.adresse,
      ville: this.demandeForm.value.ville,
      region: this.demandeForm.value.region,
      telephone: this.demandeForm.value.telephone,
      email: this.demandeForm.value.email,
    };

    formData.append('demande', JSON.stringify(demandeData));

    if (this.photoIdentite) {
      formData.append('photoIdentite', this.photoIdentite);
    }
    if (this.ancienneCni) {
      formData.append('ancienneCni', this.ancienneCni);
    }

    console.log('Envoi de la demande avec fichiers...');

    this.demandeService.creerDemande(formData).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        console.log('Réponse API:', response);

        Swal.fire({
          title: 'Succès !',
          text: 'Votre demande de CNI a été soumise avec succès',
          icon: 'success',
          confirmButtonText: 'OK'
        }).then(() => {
          this.router.navigate(['/user/mes-demandes']);
        });
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Erreur API:', error);
        this.handleError(error);
      }
    });
  }

  onFileChange(event: any, field: string) {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      Swal.fire('Erreur', 'Veuillez sélectionner une image valide (JPEG, PNG)', 'error');
      this.clearFileInput(event.target);
      return;
    }

    if (file.size > 2 * 1024 * 1024) {
      Swal.fire('Erreur', 'La taille du fichier ne doit pas dépasser 2MB', 'error');
      this.clearFileInput(event.target);
      return;
    }

    if (field === 'photoIdentite') {
      this.photoIdentite = file;
      this.demandeForm.patchValue({ photoIdentite: file }); // Pour la validation
      this.demandeForm.get('photoIdentite')?.updateValueAndValidity();
      this.createPreview(file, 'photoIdentite');
    } else if (field === 'ancienneCni') {
      this.ancienneCni = file;
      this.demandeForm.patchValue({ ancienneCni: file });
      this.demandeForm.get('ancienneCni')?.updateValueAndValidity();
      this.createPreview(file, 'ancienneCni');
    }
  }

  removePhoto() {
    this.photoIdentite = null;
    this.photoPreview = null;
    this.demandeForm.patchValue({ photoIdentite: null });
    this.demandeForm.get('photoIdentite')?.updateValueAndValidity();
  }

  removeAncienneCni() {
    this.ancienneCni = null;
    this.ancienneCniPreview = null;
    this.demandeForm.patchValue({ ancienneCni: null });
    this.demandeForm.get('ancienneCni')?.updateValueAndValidity();
  }

  private createPreview(file: File, field: string) {
    const reader = new FileReader();
    reader.onload = () => {
      if (field === 'photoIdentite') {
        this.photoPreview = reader.result as string;
      } else if (field === 'ancienneCni') {
        this.ancienneCniPreview = reader.result as string;
      }
    };
    reader.readAsDataURL(file);
  }

  private clearFileInput(input: HTMLInputElement) {
    input.value = '';
  }

  private handleError(error: any) {
    let errorMessage = 'Une erreur est survenue lors de la soumission';

    if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.status === 401) {
      errorMessage = 'Veuillez vous reconnecter';
    } else if (error.status === 400) {
      errorMessage = 'Données invalides. Vérifiez les informations saisies.';
    } else if (error.status === 0) {
      errorMessage = 'Impossible de contacter le serveur. Vérifiez votre connexion.';
    }

    Swal.fire({
      title: 'Erreur',
      text: errorMessage,
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }

  private showFormErrors() {
    const invalidFields: string[] = [];

    Object.keys(this.demandeForm.controls).forEach(key => {
      const control = this.demandeForm.get(key);
      if (control?.invalid) {
        invalidFields.push(this.getFieldLabel(key));
      }
    });

    if (invalidFields.length > 0) {
      Swal.fire({
        title: 'Formulaire incomplet',
        html: `Veuillez remplir les champs suivants :<br><strong>${invalidFields.join(', ')}</strong>`,
        icon: 'warning',
        confirmButtonText: 'OK'
      });
    }
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      typeDemandeCni: 'Type de demande',
      nom: 'Nom',
      prenoms: 'Prénoms',
      profession: 'Profession',
      sexe: 'Sexe',
      dateNaissance: 'Date de naissance',
      lieuNaissance: 'Lieu de naissance',
      taille: 'Taille',
      adresse: 'Adresse',
      ville: 'Ville',
      region: 'Région',
      telephone: 'Téléphone',
      email: 'Email',
      photoIdentite: 'Photo d\'identité',
      consentementTraitement: 'Consentement traitement',
      consentementVerification: 'Certification informations'
    };

    return labels[fieldName] || fieldName;
  }

  markFormGroupTouched() {
    Object.keys(this.demandeForm.controls).forEach(key => {
      this.demandeForm.get(key)?.markAsTouched();
    });
  }

  get f() { return this.demandeForm.controls; }
}
