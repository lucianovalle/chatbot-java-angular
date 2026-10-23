import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChatInfo, ChatReply } from './chat.models';

/**
 * Única porta do Angular para o Java.
 * As URLs começam com /api. O proxy.conf.json encaminha isso
 * para http://localhost:8080 enquanto o ng serve está rodando.
 */
@Injectable({ providedIn: 'root' })
export class ChatService {
  private readonly http = inject(HttpClient);

  info(): Observable<ChatInfo> {
    return this.http.get<ChatInfo>('/api/chat/info');
  }

  send(message: string): Observable<ChatReply> {
    return this.http.post<ChatReply>('/api/chat', { message });
  }

  reset(): Observable<void> {
    return this.http.post<void>('/api/chat/reset', {});
  }
}
