import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { BeneficioService } from '../../services/beneficio.service';
import { BeneficioDTO } from '../../models/beneficio.dto';
import { PageDTO } from '../../models/page.dto';
import { TransferenciaDialogComponent } from '../transferencia-dialog/transferencia-dialog';
import { BeneficioFormComponent } from '../beneficio-form/beneficio-form';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatSnackBarModule,
    MatPaginatorModule
  ],
  templateUrl: './beneficio-list.html',
  styleUrl: './beneficio-list.css'
})
export class BeneficioListComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  
  displayedColumns: string[] = ['id', 'nome', 'descricao', 'valor', 'ativo', 'acoes'];
  beneficios: BeneficioDTO[] = [];
  loading = false;
  
  pageSize = 10;
  pageIndex = 0;
  totalElements = 0;

  constructor(
    private beneficioService: BeneficioService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.loading = true;
    this.beneficioService.listarTodosPaginados(this.pageIndex, this.pageSize).subscribe({
      next: (page: PageDTO<BeneficioDTO>) => {
        this.beneficios = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar benefícios:', error);
        this.snackBar.open('Erro ao carregar benefícios', 'Fechar', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.carregarBeneficios();
  }

  criarNovo(): void {
    const dialogRef = this.dialog.open(BeneficioFormComponent, {
      width: '500px',
      data: null
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.carregarBeneficios();
      }
    });
  }

  editar(beneficio: BeneficioDTO): void {
    const dialogRef = this.dialog.open(BeneficioFormComponent, {
      width: '500px',
      data: beneficio
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.carregarBeneficios();
      }
    });
  }

  remover(beneficio: BeneficioDTO): void {
    if (confirm(`Deseja realmente remover o benefício "${beneficio.nome}"?`)) {
      if (beneficio.id) {
        this.beneficioService.remover(beneficio.id).subscribe({
          next: () => {
            this.snackBar.open('Benefício removido com sucesso', 'Fechar', { duration: 3000 });
            this.carregarBeneficios();
          },
          error: (error) => {
            console.error('Erro ao remover benefício:', error);
            this.snackBar.open('Erro ao remover benefício', 'Fechar', { duration: 3000 });
          }
        });
      }
    }
  }

  transferir(): void {
    const dialogRef = this.dialog.open(TransferenciaDialogComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.carregarBeneficios();
      }
    });
  }

  formatarMoeda(valor: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor);
  }
}
