import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Demande } from '../../shared/models/demande.model';

@Injectable({
  providedIn: 'root',
})
export class DemandeService {
  private apiUrl = 'http://localhost:5050/api/demandes';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  /**
   *  CRÉER UNE DEMANDE (Unifié - pour tous les types)
   */
  creerDemande(formData: FormData): Observable<Demande> {
    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<Demande>(
      `${this.apiUrl}/avec-fichiers`,
      formData,
      { headers: headers }
    );
  }

  /**
   * RÉCUPÉRER MES DEMANDES (Tous types)
   */
  getMesDemandes(): Observable<Demande[]> {
    return this.http.get<Demande[]>(
      `${this.apiUrl}/mes-demandes`,
      { headers: this.getHeaders() }
    );
  }

  /**
   *  RÉCUPÉRER MES DEMANDES PAR TYPE
   */
  getMesDemandesParType(type: string): Observable<Demande[]> {
    return this.http.get<Demande[]>(
      `${this.apiUrl}/mes-demandes/${type}`,
      { headers: this.getHeaders() }
    );
  }

  /**
   *  RÉCUPÉRER UNE DEMANDE SPÉCIFIQUE
   */
  getDemande(id: string): Observable<Demande> {
    return this.http.get<Demande>(
      `${this.apiUrl}/${id}`,
      { headers: this.getHeaders() }
    );
  }

  /**
   *  LISTER TOUTES LES DEMANDES (Admin)
   */
  listerToutesDemandes(): Observable<Demande[]> {
    return this.http.get<Demande[]>(
      `${this.apiUrl}/les-demandes`,
      { headers: this.getHeaders() }
    );
  }

  /**
   *  LISTER DEMANDES PAR TYPE (Admin)
   */
  listerDemandesParType(type: string): Observable<Demande[]> {
    return this.http.get<Demande[]>(
      `${this.apiUrl}/type/${type}`,
      { headers: this.getHeaders() }
    );
  }

  /**
   *  VALIDER UNE DEMANDE (Admin)
   */
  validerDemande(id: string): Observable<Demande> {
    return this.http.put<Demande>(
      `${this.apiUrl}/${id}/valider`,
      {},
      { headers: this.getHeaders().set('Content-Type', 'application/json') }
    );
  }

  /**
   *  TÉLÉCHARGER LE PDF CNI
   */
  telechargerCniPdf(id: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/cni/${id}/pdf`, {
      headers: this.getHeaders(),
      responseType: 'blob' // très important pour récupérer le fichier binaire
    });
  }

  /**
   *  REJETER UNE DEMANDE (Admin)
   */
  rejeterDemande(id: string, commentaire: string): Observable<Demande> {
    return this.http.put<Demande>(
      `${this.apiUrl}/${id}/rejeter`,
      commentaire,
      {
        headers: this.getHeaders().set('Content-Type', 'application/json')
      }
    );
  }
}
