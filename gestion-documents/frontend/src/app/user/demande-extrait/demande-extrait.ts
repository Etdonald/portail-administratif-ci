import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-demande-extrait',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, ReactiveFormsModule],
  templateUrl: './demande-extrait.html',
  styleUrls: ['./demande-extrait.css']
})
export class DemandeExtrait {
  extraitForm: FormGroup;
  isSubmitting = false;

  typesExtrait = [
    {
      value: 'COPIE_INTEGRALE',
      label: 'Copie Intégrale',
      description: 'Reproduction complète de l\'acte'
    },
    {
      value: 'EXTRATI_SANS_FILIATION',
      label: 'Extrait sans filiation',
      description: 'Sans mentions des parents'
    },
    {
      value: 'EXTRATI_AVEC_FILIATION',
      label: 'Extrait avec filiation',
      description: 'Avec mentions des parents'
    }
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.extraitForm = this.createForm();
  }

  createForm(): FormGroup {
    return this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      prenoms: ['', [Validators.required, Validators.minLength(2)]],
      dateNaissance: ['', Validators.required],
      lieuNaissance: ['Abidjan', Validators.required],
      sexe: ['', Validators.required],
      numeroActe: [''],

      nomPere: ['', Validators.required],
      prenomsPere: ['', Validators.required],
      nomMere: ['', Validators.required],
      prenomsMere: ['', Validators.required],
      professionParents: [''],

      demandeurNom: ['', Validators.required],
      lienParente: ['', Validators.required],
      telephone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      email: ['', Validators.email],
      adresse: ['', Validators.required],

      // Type d'extrait
      typeExtrait: ['COPIE_INTEGRALE', Validators.required],

      certificationExactitude: [false, Validators.requiredTrue],
      consentementTraitement: [false, Validators.requiredTrue]
    });
  }

  onSubmit() {
    if (this.extraitForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this.isSubmitting = true;
  }

  onFileChange(event: any, field: string) {
  }

  markFormGroupTouched() {
    Object.keys(this.extraitForm.controls).forEach(key => {
      this.extraitForm.get(key)?.markAsTouched();
    });
  }

  get f() { return this.extraitForm.controls; }
}
