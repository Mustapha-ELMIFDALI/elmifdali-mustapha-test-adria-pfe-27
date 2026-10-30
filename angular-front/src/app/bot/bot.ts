import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MarkdownComponent } from 'ngx-markdown';
import { BankApi } from '../services/bank-api';

interface ChatMessage {
  fromUser: boolean;
  text: string;
}

@Component({
  selector: 'app-bot',
  imports: [FormsModule, MarkdownComponent],
  templateUrl: './bot.html',
})
export class Bot {
  private api = inject(BankApi);
  // Each page visit gets its own memory on the server
  private conversationId = crypto.randomUUID();

  messages = signal<ChatMessage[]>([]);
  waiting = signal(false);
  query = '';

  send(): void {
    const text = this.query.trim();
    if (!text || this.waiting()) {
      return;
    }
    this.query = '';
    this.add(true, text);
    this.waiting.set(true);
    this.api.chat(text, this.conversationId).subscribe({
      next: (reply) => {
        this.add(false, reply);
        this.waiting.set(false);
      },
      error: () => {
        this.add(false, "L'assistant est indisponible. Vérifiez vos transactions avant de recommencer.");
        this.waiting.set(false);
      },
    });
  }

  private add(fromUser: boolean, text: string): void {
    this.messages.update((messages) => [...messages, { fromUser, text }]);
  }
}
