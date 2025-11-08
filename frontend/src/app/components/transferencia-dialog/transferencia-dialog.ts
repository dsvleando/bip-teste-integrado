import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Observable, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, startWith } from 'rxjs/operators';
import { BeneficioService } from '../../services/beneficio.service';
import { BeneficioSearchDTO } from '../../models/beneficio-search.dto';
import { TransferenciaDTO } from '../../models/transferencia.dto';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';

@Component({
  selector: 'app-transferencia-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatSnackBarModule,
    CurrencyInputComponent
  ],
  templateUrl: './transferencia-dialog.html',
  styleUrl: './transferencia-dialog.css'
})
export class TransferenciaDialogComponent implements OnInit {
  transferenciaForm: FormGroup;
  origemOptions$: Observable<BeneficioSearchDTO[]> = of([]);
  destinoOptions$: Observable<BeneficioSearchDTO[]> = of([]);
  origemControl = new FormControl<string | BeneficioSearchDTO | null>('');
  destinoControl = new FormControl<string | BeneficioSearchDTO | null>('');

  constructor(
    private fb: FormBuilder,
    private beneficioService: BeneficioService,
    private dialogRef: MatDialogRef<TransferenciaDialogComponent>,
    private snackBar: MatSnackBar
  ) {
    this.transferenciaForm = this.fb.group({
      origem: ['', Validators.required],
      destino: ['', Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    this.origemControl = this.transferenciaForm.get('origem') as FormControl<string | BeneficioSearchDTO | null>;
    this.destinoControl = this.transferenciaForm.get('destino') as FormControl<string | BeneficioSearchDTO | null>;

    this.origemOptions$ = this.origemControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((value: string | BeneficioSearchDTO | null) => {
        if (typeof value !== 'string') {
          return of([]);
        }
        const searchValue = value.trim();
        if (searchValue && searchValue.length >= 2) {
          return this.beneficioService.buscarAtivosPorNome(searchValue);
        }
        return of([]);
      })
    );

    this.destinoOptions$ = this.destinoControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((value: string | BeneficioSearchDTO | null) => {
        if (typeof value !== 'string') {
          return of([]);
        }
        const searchValue = value.trim();
        if (searchValue && searchValue.length >= 2) {
          return this.beneficioService.buscarAtivosPorNome(searchValue);
        }
        return of([]);
      })
    );

    this.origemControl.valueChanges.subscribe(() => {
      this.validateBeneficios();
    });
    this.destinoControl.valueChanges.subscribe(() => {
      this.validateBeneficios();
    });
  }

  displayBeneficio(beneficio: BeneficioSearchDTO | null): string {
    return beneficio ? beneficio.nome : '';
  }

  transferir(): void {
    if (this.transferenciaForm.valid) {
      const origem = this.origemControl.value as BeneficioSearchDTO;
      const destino = this.destinoControl.value as BeneficioSearchDTO;

      if (!origem || !destino || !origem.id || !destino.id) {
        this.snackBar.open('Selecione origem e destino válidos', 'Fechar', { duration: 3000 });
        return;
      }

      const transferencia: TransferenciaDTO = {
        fromId: origem.id,
        toId: destino.id,
        amount: this.transferenciaForm.value.amount
      };

      this.beneficioService.transferir(transferencia).subscribe({
        next: () => {
          this.snackBar.open('Transferência realizada com sucesso', 'Fechar', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: (error) => {
          console.error('Erro ao transferir:', error);
          const mensagem = error.error?.message || 'Erro ao realizar transferência';
          this.snackBar.open(mensagem, 'Fechar', { duration: 5000 });
        }
      });
    }
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }

  getErrorMessage(fieldName: string): string {
    const field = fieldName === 'origem' ? this.origemControl : this.destinoControl;
    if (field.hasError('required')) {
      return fieldName === 'origem' ? 'Benefício de origem é obrigatório' : 'Benefício de destino é obrigatório';
    }
    if (field.hasError('sameBeneficio')) {
      return 'Origem e destino devem ser diferentes';
    }
    return '';
  }

  validateBeneficios(): void {
    const origem = this.origemControl.value as BeneficioSearchDTO;
    const destino = this.destinoControl.value as BeneficioSearchDTO;

    if (origem && destino && origem.id && destino.id && origem.id === destino.id) {
      this.destinoControl.setErrors({ sameBeneficio: true });
    } else {
      const errors = this.destinoControl.errors;
      if (errors && errors['sameBeneficio']) {
        const newErrors = { ...errors };
        delete newErrors['sameBeneficio'];
        this.destinoControl.setErrors(Object.keys(newErrors).length > 0 ? newErrors : null);
      }
    }
  }
}
