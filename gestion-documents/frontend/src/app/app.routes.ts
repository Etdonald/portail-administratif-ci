import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Acceuil } from './pages/acceuil/acceuil';
import { AuthGuard } from './auth/guards/auth.guard';
import { RoleGuard } from './auth/guards/role.guard';
import { Inscription } from './pages/inscription/inscription';
import { Activation } from './pages/activation/activation';
import {DemandeExtrait} from './user/demande-extrait/demande-extrait';
import {DemandeCni} from './user/demande-cni/demande-cni';
import {DemandePermis} from './user/demande-permis/demande-permis';
import {MesDemandes} from './user/mes-demandes/mes-demandes';
import {GestionDemandes} from './admin/gestion-demandes/gestion-demandes';
import {GestionUtilisateurs} from './admin/gestion-utilisateurs/gestion-utilisateurs';
import {Dashboard} from './admin/dashboard/dashboard';
import {Profil} from './shared/profil/profil';
import {Aide} from './shared/aide/aide';

export const routes: Routes = [
  { path: '', redirectTo: 'acceuil', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'inscription', component: Inscription },
  { path: 'activation', component: Activation },
  { path: 'acceuil', component: Acceuil, canActivate: [AuthGuard] },

  {
    path: 'demande-extrait',
    component: DemandeExtrait,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_USER'] }
  },
  {
    path: 'demande-cni',
    component: DemandeCni,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_USER'] }
  },
  {
    path: 'demande-permis',
    component: DemandePermis,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_USER'] }
  },
  {
    path: 'mes-demandes',
    component: MesDemandes,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_USER'] }
  },

  {
    path: 'gestion-demandes',
    component: GestionDemandes,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'gestion-utilisateurs',
    component: GestionUtilisateurs,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },

  {
    path: 'profil',
    component: Profil,
    canActivate: [AuthGuard]
  },
  {
    path: 'aide',
    component: Aide,
  },

  { path: '**', redirectTo: 'acceuil' }
];
