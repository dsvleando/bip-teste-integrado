import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BeneficioService } from '../../services/beneficio.service';
import { BeneficioDTO } from '../../models/beneficio.dto';
import { CurrencyInputComponent } from '../../shared/components/currency-input/currency-input.component';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatSnackBarModule,
    CurrencyInputComponent
  ],
  templateUrl: './beneficio-form.html',
  styleUrl: './beneficio-form.css'
})
export class BeneficioFormComponent implements OnInit {
  beneficioForm: FormGroup;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private beneficioService: BeneficioService,
    private dialogRef: MatDialogRef<BeneficioFormComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: BeneficioDTO | null
  ) {
    this.beneficioForm = this.fb.group({
      nome: ['', [Validators.required, Validators.maxLength(100)]],
      descricao: ['', [Validators.maxLength(255)]],
      valor: [null, [Validators.required, Validators.min(0.01)]],
      ativo: [true]
    });
  }

  ngOnInit(): void {
    if (this.data) {
      this.isEditMode = true;
      this.beneficioForm.patchValue({
        nome: this.data.nome,
        descricao: this.data.descricao || '',
        valor: this.data.valor ?? null,
        ativo: this.data.ativo ?? true
      });
      if (this.data.version !== undefined) {
        this.beneficioForm.addControl('version', this.fb.control(this.data.version));
      }
    }
  }

  salvar(): void {
    if (this.beneficioForm.valid) {
      const formValue = this.beneficioForm.value;
      const beneficio: BeneficioDTO = {
        ...formValue,
        valor: formValue.valor
      };

      if (this.isEditMode && this.data?.id) {
        const beneficioComVersion = {
          ...beneficio,
          version: this.beneficioForm.get('version')?.value ?? this.data.version
        };
        this.beneficioService.atualizar(this.data.id, beneficioComVersion).subscribe({
          next: () => {
            this.snackBar.open('Benefício atualizado com sucesso', 'Fechar', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Erro ao atualizar benefício:', error);
            const mensagem = error.error?.message || 'Erro ao atualizar benefício';
            this.snackBar.open(mensagem, 'Fechar', { duration: 5000 });
          }
        });
      } else {
        this.beneficioService.criar(beneficio).subscribe({
          next: () => {
            this.snackBar.open('Benefício criado com sucesso', 'Fechar', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            console.error('Erro ao criar benefício:', error);
            const mensagem = error.error?.message || 'Erro ao criar benefício';
            this.snackBar.open(mensagem, 'Fechar', { duration: 5000 });
          }
        });
      }
    }
  }

  cancelar(): void {
    this.dialogRef.close(false);
  }
  getErrorMessage(fieldName: string): string {
    const field = this.beneficioForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} é obrigatório`;
    }
    if (field?.hasError('maxlength')) {
      return `Máximo de ${field.getError('maxlength').requiredLength} caracteres`;
    }
    return '';
  }
}
