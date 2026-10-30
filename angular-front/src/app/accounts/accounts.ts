import { Component, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Account } from '../model/bank.model';
import { BankApi } from '../services/bank-api';

@Component({
  selector: 'app-accounts',
  imports: [DecimalPipe],
  templateUrl: './accounts.html',
})
export class Accounts implements OnInit {
  private api = inject(BankApi);

  accounts = signal<Account[]>([]);
  loading = signal(true);
  error = signal('');

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.api.accounts().subscribe({
      next: (accounts) => {
        this.accounts.set(accounts);
        this.error.set('');
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(this.api.errorMessage(e));
        this.loading.set(false);
      },
    });
  }

  toggleStatus(account: Account): void {
    const request = account.status === 'ACTIVE' ? this.api.block(account.id) : this.api.activate(account.id);
    request.subscribe({
      next: () => this.load(),
      error: (e) => this.error.set(this.api.errorMessage(e)),
    });
  }
}
