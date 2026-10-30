import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Account, PaymentRequest, Transaction, TransferRequest } from '../model/bank.model';

@Injectable({ providedIn: 'root' })
export class BankApi {
  private http = inject(HttpClient);
  private accountsUrl = `${environment.apiUrl}/ACCOUNT-SERVICE/accounts`;
  private transactionsUrl = `${environment.apiUrl}/TRANSACTION-SERVICE/transactions`;
  private chatUrl = `${environment.apiUrl}/CHATBOT-SERVICE/chat`;

  accounts() {
    return this.http.get<Account[]>(this.accountsUrl);
  }

  block(id: string) {
    return this.http.patch<Account>(`${this.accountsUrl}/${id}/block`, null);
  }

  activate(id: string) {
    return this.http.patch<Account>(`${this.accountsUrl}/${id}/activate`, null);
  }

  transactions() {
    return this.http.get<Transaction[]>(this.transactionsUrl);
  }

  pay(request: PaymentRequest) {
    return this.http.post<Transaction>(this.transactionsUrl, request);
  }

  transfer(request: TransferRequest) {
    return this.http.post<Transaction[]>(`${this.transactionsUrl}/transfer`, request);
  }

  chat(query: string, conversationId: string) {
    return this.http.get(this.chatUrl, { params: { query, conversationId }, responseType: 'text' });
  }

  errorMessage(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'Impossible de joindre le serveur.';
    }
    return error.error?.detail ?? 'Une erreur est survenue, réessayez plus tard.';
  }
}
