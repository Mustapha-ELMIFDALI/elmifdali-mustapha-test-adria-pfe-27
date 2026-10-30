import { Routes } from '@angular/router';
import { Accounts } from './accounts/accounts';
import { Transactions } from './transactions/transactions';
import { Bot } from './bot/bot';

export const routes: Routes = [
  { path: '', redirectTo: 'accounts', pathMatch: 'full' },
  { path: 'accounts', component: Accounts },
  { path: 'transactions', component: Transactions },
  { path: 'bot', component: Bot },
];
