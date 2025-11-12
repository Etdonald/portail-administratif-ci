import { Routes } from '@angular/router';
import {Login} from './auth/login/login';
import {Acceuil} from './pages/acceuil/acceuil';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login},
  { path: 'acceuil', component: Acceuil },
];
