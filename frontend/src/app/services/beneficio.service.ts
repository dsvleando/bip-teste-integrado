import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BeneficioDTO } from '../models/beneficio.dto';
import { BeneficioSearchDTO } from '../models/beneficio-search.dto';
import { TransferenciaDTO } from '../models/transferencia.dto';
import { PageDTO } from '../models/page.dto';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private apiUrl = `${environment.apiUrl}/beneficios`;

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  listarTodosPaginados(page: number = 0, size: number = 10): Observable<PageDTO<BeneficioDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageDTO<BeneficioDTO>>(`${this.apiUrl}`, { params });
  }

  buscarPorId(id: number): Observable<BeneficioDTO> {
    return this.http.get<BeneficioDTO>(`${this.apiUrl}/${id}`);
  }

  criar(beneficio: BeneficioDTO): Observable<BeneficioDTO> {
    return this.http.post<BeneficioDTO>(this.apiUrl, beneficio, this.httpOptions);
  }

  atualizar(id: number, beneficio: BeneficioDTO): Observable<BeneficioDTO> {
    return this.http.put<BeneficioDTO>(`${this.apiUrl}/${id}`, beneficio, this.httpOptions);
  }

  remover(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  transferir(transferencia: TransferenciaDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transfer`, transferencia, this.httpOptions);
  }

  buscarAtivosPorNome(nome: string): Observable<BeneficioSearchDTO[]> {
    const params = new HttpParams().set('nome', nome);
    return this.http.get<BeneficioSearchDTO[]>(`${this.apiUrl}/search`, { params });
  }
}

