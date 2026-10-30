export interface Account {
  id: string;
  customerReference: string;
  status: 'ACTIVE' | 'BLOCKED';
  currency: string;
  balance: number;
}

export interface Transaction {
  id: string;
  accountId: string;
  type: 'PAYMENT' | 'TRANSFER_OUT' | 'TRANSFER_IN';
  amount: number;
  currency: string;
  merchantCategory: string | null;
  country: string | null;
  createdAt: string;
}

export interface PaymentRequest {
  accountId: string;
  amount: number;
  currency: string;
  merchantCategory: string;
  country: string;
}

export interface TransferRequest {
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  currency: string;
}
