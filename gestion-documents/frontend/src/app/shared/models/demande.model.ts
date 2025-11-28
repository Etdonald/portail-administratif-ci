export interface Demande {
  id?: string;

  typeDemande: 'CNI' | 'EXTRAIT' | 'PERMIS';
  typeDemandeCni?: 'PREMIERE_DEMANDE' | 'RENOUVELLEMENT' | 'DUPLICATA';
  statut?: 'EN_ATTENTE' | 'APPROUVEE' | 'REJETEE';
  traitee?: boolean;
  commentaireAdmin?: string;
  dateCreation?: string;
  dateSoumission?: string;
  utilisateurId?: string;

  dateNaissance: string;
  lieuNaissance: string;

  nom?: string;
  prenoms?: string;
  profession?: string;
  sexe?: string;
  nationalite?: string;
  taille?: string;

  adresse?: string;
  ville?: string;
  region?: string;
  telephone?: string;
  email?: string;

  numeroCNI?: string;
  numeroNNI?: string;
  dateEmission?: string;
  dateExpiration?: string;
  lieuEmission?: string;
  autoriteEmettrice?: string;

  photoIdentitePath?: string;
  ancienneCNIPath?: string;

  photoIdentite?: File;
  ancienneCNI?: File;
  cheminCniPdf?: string;
}
