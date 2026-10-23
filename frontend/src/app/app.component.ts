import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatMessage } from './chat.models';
import { ChatService } from './chat.service';

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  private readonly chat = inject(ChatService);

  readonly messages = signal<ChatMessage[]>([]);
  readonly sending = signal(false);
  readonly error = signal<string | null>(null);
  readonly modelName = signal('...');

  draft = '';

  ngOnInit(): void {
    this.chat.info().subscribe({
      next: (info) => this.modelName.set(info.modelName),
      error: () => this.modelName.set('backend parado'),
    });
  }

  send(): void {
    const message = this.draft.trim();
    if (!message || this.sending()) {
      return;
    }

    this.draft = '';
    this.error.set(null);
    this.messages.update((list) => [...list, { role: 'user', text: message }]);
    this.sending.set(true);

    this.chat.send(message).subscribe({
      next: (reply) => {
        this.messages.update((list) => [...list, { role: 'bot', text: reply.answer }]);
        this.sending.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.error.set(this.describe(err));
        this.sending.set(false);
      },
    });
  }

  clear(): void {
    this.chat.reset().subscribe({
      next: () => {
        this.messages.set([]);
        this.error.set(null);
      },
      error: (err: HttpErrorResponse) => this.error.set(this.describe(err)),
    });
  }

  private describe(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return 'O backend Java não respondeu. Ele precisa estar em http://localhost:8080.';
    }
    const body = err.error as { message?: string } | null;
    return body?.message ?? 'A chamada para /api/chat falhou.';
  }
}
