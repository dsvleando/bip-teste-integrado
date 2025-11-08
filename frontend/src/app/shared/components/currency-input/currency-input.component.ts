import { Component, Input, Optional, SkipSelf, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, NgControl } from '@angular/forms';

@Component({
  selector: 'app-currency-input',
  standalone: true,
  imports: [CommonModule, MatFormFieldModule, MatInputModule],
  template: `
    <mat-form-field appearance="outline" class="currency-input">
      <mat-label>{{ label }}</mat-label>
      <input
        matInput
        [placeholder]="placeholder"
        [value]="displayValue"
        [required]="required"
        [disabled]="disabled"
        inputmode="decimal"
        (input)="onInput($event.target.value)"
        (blur)="onBlur()"
      />
      <ng-container *ngIf="shouldShowError('required')">
        <mat-error>{{ label }} é obrigatório</mat-error>
      </ng-container>
      <ng-container *ngIf="shouldShowError('min')">
        <mat-error>Valor deve ser maior que zero</mat-error>
      </ng-container>
    </mat-form-field>
  `,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CurrencyInputComponent),
      multi: true
    }
  ]
})
export class CurrencyInputComponent implements ControlValueAccessor {
  @Input() label = 'Valor';
  @Input() placeholder = '';
  @Input() required = false;
  @Input() min = 0.01;

  displayValue = '';
  disabled = false;

  private onChange: (value: number | null) => void = () => {};
  private onTouched: () => void = () => {};

  constructor(@Optional() @SkipSelf() private ngControl: NgControl | null) {
    if (this.ngControl) {
      this.ngControl.valueAccessor = this;
    }
  }

  writeValue(value: number | string | null): void {
    if (value === null || value === undefined || value === '') {
      this.displayValue = '';
      return;
    }

    const numeric = typeof value === 'number' ? value : this.normalizeValue(value.toString());
    this.displayValue = numeric !== null ? this.formatValue(numeric) : value.toString();
  }

  registerOnChange(fn: (value: number | null) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  onInput(rawValue: string): void {
    const sanitized = this.sanitize(rawValue);
    this.displayValue = sanitized;
    const normalized = this.normalizeValue(sanitized);
    this.onChange(normalized);
  }

  onBlur(): void {
    const normalized = this.normalizeValue(this.displayValue);
    if (normalized !== null && normalized >= this.min) {
      this.displayValue = this.formatValue(normalized);
    }
    this.onTouched();
  }

  shouldShowError(type: string): boolean {
    const control = this.ngControl?.control;
    if (!control) {
      return false;
    }
    return control.hasError(type) && (control.dirty || control.touched);
  }

  private sanitize(valor: string): string {
    if (valor === null || valor === undefined) {
      return '';
    }

    let texto = valor.replace(/[^0-9,.\s]/g, '');
    texto = texto.replace(/\s/g, '');
    texto = texto.replace('.', ',');

    const partes = texto.split(',');
    let inteiro = partes[0]?.replace(/[^\d]/g, '') ?? '';
    let decimal = partes[1]?.replace(/[^\d]/g, '') ?? '';

    decimal = decimal.slice(0, 2);

    if (inteiro === '' && decimal.length > 0) {
      inteiro = '0';
    }

    if (texto.includes(',')) {
      return `${inteiro}${decimal.length ? ',' + decimal : ''}`;
    }

    return inteiro;
  }

  private normalizeValue(valor: string): number | null {
    if (!valor) {
      return null;
    }

    const texto = valor.replace(',', '.');
    const numero = Number(texto);
    if (!Number.isFinite(numero)) {
      return null;
    }

    return Number(numero.toFixed(2));
  }

  private formatValue(valor: number): string {
    return valor.toFixed(2).replace('.', ',');
  }
}
