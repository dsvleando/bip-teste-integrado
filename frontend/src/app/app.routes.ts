import { Routes } from '@angular/router';
import { BeneficioListComponent } from './components/beneficio-list/beneficio-list';

export const routes: Routes = [
  { path: '', component: BeneficioListComponent },
  { path: '**', redirectTo: '' }
];
