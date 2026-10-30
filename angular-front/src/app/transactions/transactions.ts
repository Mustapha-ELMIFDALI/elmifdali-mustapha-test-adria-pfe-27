import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Account, Transaction } from '../model/bank.model';
import { BankApi } from '../services/bank-api';

@Component({
  selector: 'app-transactions',
  imports: [DatePipe, DecimalPipe, FormsModule],
  templateUrl: './transactions.html',
})
export class Transactions implements OnInit {
  private api = inject(BankApi);

  accounts = signal<Account[]>([]);
  transactions = signal<Transaction[]>([]);
  latestFirst = computed(() => [...this.transactions()].sort((a, b) => b.createdAt.localeCompare(a.createdAt)));
  error = signal('');
  success = signal('');

  payment = { accountId: '', amount: null as number | null, merchantCategory: '', country: '' };
  transfer = { fromAccountId: '', toAccountId: '', amount: null as number | null };

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.accounts().subscribe({
      next: (accounts) => this.accounts.set(accounts),
      error: (e) => this.error.set(this.api.errorMessage(e)),
    });
    this.api.transactions().subscribe({
      next: (transactions) => this.transactions.set(transactions),
      error: (e) => this.error.set(this.api.errorMessage(e)),
    });
  }

  label(account: Account): string {
    return `${account.customerReference.slice(-2)} - ${account.balance.toFixed(2)} ${account.currency}`;
  }

  accountNumber(accountId: string): string {
    return this.accounts().find((account) => account.id === accountId)?.customerReference.slice(-2) ?? '-';
  }

  private currencyOf(accountId: string): string {
    return this.accounts().find((account) => account.id === accountId)?.currency ?? '';
  }

  submitPayment(): void {
    this.api
      .pay({ ...this.payment, amount: this.payment.amount!, currency: this.currencyOf(this.payment.accountId) })
      .subscribe({
        next: () => {
          this.done('Paiement enregistré.');
          this.payment = { accountId: '', amount: null, merchantCategory: '', country: '' };
        },
        error: (e) => this.failed(e),
      });
  }

  submitTransfer(): void {
    this.api
      .transfer({ ...this.transfer, amount: this.transfer.amount!, currency: this.currencyOf(this.transfer.fromAccountId) })
      .subscribe({
        next: () => {
          this.done('Virement effectué.');
          this.transfer = { fromAccountId: '', toAccountId: '', amount: null };
        },
        error: (e) => this.failed(e),
      });
  }

  private done(message: string): void {
    this.error.set('');
    this.success.set(message);
    this.load();
  }

  private failed(error: HttpErrorResponse): void {
    this.success.set('');
    this.error.set(this.api.errorMessage(error));
  }
}
