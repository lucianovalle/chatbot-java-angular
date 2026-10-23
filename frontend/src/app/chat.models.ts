export interface ChatMessage {
  role: 'user' | 'bot';
  text: string;
}

export interface ChatReply {
  answer: string;
}

export interface ChatInfo {
  modelName: string;
  baseUrl: string;
}
