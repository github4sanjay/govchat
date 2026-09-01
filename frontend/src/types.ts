export type User = {
  id: string
  username: string
  displayName: string
}

export type Message = {
  id: string
  clientMessageId: string
  senderId: string
  recipientId: string
  content: string
  sentAt: string
}

export type ConnectionStatus = 'offline' | 'connecting' | 'online'

export type ServerEvent =
  | { type: 'CONNECTED' }
  | { type: 'MESSAGE'; message: Message }
  | { type: 'ERROR'; error: string }
