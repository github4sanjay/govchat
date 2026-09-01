import { computed, onUnmounted, ref, watch, type Ref } from 'vue'
import { getJson } from '../api/api'
import type { ConnectionStatus, Message, ServerEvent, User } from '../types'

export function useMessaging(me: Ref<User | null>) {
  const users = ref<User[]>([])
  const peerId = ref('')
  const messages = ref<Message[]>([])
  const status = ref<ConnectionStatus>('offline')
  const error = ref('')
  let socket: WebSocket | null = null

  const peers = computed(() => users.value.filter(user => user.id !== me.value?.id))
  const peer = computed(() => users.value.find(user => user.id === peerId.value))
  const visibleMessages = computed(() => messages.value.filter(message =>
    me.value && ((message.senderId === me.value.id && message.recipientId === peerId.value) ||
      (message.senderId === peerId.value && message.recipientId === me.value.id))))

  function socketUrl() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    return `${protocol}//${window.location.host}/ws`
  }

  function mergeMessages(incoming: Message[]) {
    const byId = new Map(messages.value.map(message => [message.id, message]))
    incoming.forEach(message => byId.set(message.id, message))
    messages.value = [...byId.values()]
      .sort((first, second) => first.sentAt.localeCompare(second.sentAt) || first.id.localeCompare(second.id))
  }

  function handleServerEvent(event: ServerEvent) {
    switch (event.type) {
      case 'CONNECTED':
        status.value = 'online'
        break
      case 'MESSAGE':
        mergeMessages([event.message])
        break
      case 'ERROR':
        error.value = event.error
        break
    }
  }

  function connectWebSocket() {
    status.value = 'connecting'
    socket = new WebSocket(socketUrl())
    socket.onmessage = event => handleServerEvent(JSON.parse(event.data) as ServerEvent)
    socket.onerror = () => { error.value = 'Real-time connection failed' }
    socket.onclose = () => { status.value = 'offline' }
  }

  function sendMessage(content: string) {
    if (!peerId.value || socket?.readyState !== WebSocket.OPEN) return
    socket.send(JSON.stringify({ type: 'SEND_MESSAGE', clientMessageId: crypto.randomUUID(), recipientId: peerId.value, content }))
    error.value = ''
  }

  watch(me, (currentUser, _, onCleanup) => {
    socket?.close()
    socket = null
    status.value = 'offline'
    messages.value = []
    if (!currentUser) {
      users.value = []
      peerId.value = ''
      return
    }

    connectWebSocket()
    const controller = new AbortController()
    getJson<User[]>('/users', { signal: controller.signal })
      .then(loaded => {
        users.value = loaded
        peerId.value = loaded.some(user => user.id === peerId.value && user.id !== currentUser.id)
          ? peerId.value
          : loaded.find(user => user.id !== currentUser.id)?.id ?? ''
      })
      .catch(reason => { if (reason.name !== 'AbortError') error.value = reason.message })
    onCleanup(() => controller.abort())
  })

  watch(peerId, (selectedPeerId, _, onCleanup) => {
    if (!me.value || !selectedPeerId || me.value.id === selectedPeerId) return
    const controller = new AbortController()
    messages.value = []
    getJson<Message[]>(`/messages?peerId=${selectedPeerId}`, { signal: controller.signal })
      .then(mergeMessages)
      .then(() => { error.value = '' })
      .catch(reason => { if (reason.name !== 'AbortError') error.value = reason.message })
    onCleanup(() => controller.abort())
  })

  onUnmounted(() => socket?.close())
  return { peers, peer, peerId, visibleMessages, status, error, sendMessage }
}
