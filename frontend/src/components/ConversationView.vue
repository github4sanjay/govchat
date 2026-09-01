<script setup lang="ts">
import { nextTick, ref, watch } from 'vue'
import MessageComposer from './MessageComposer.vue'
import type { ConnectionStatus, Message, User } from '../types'

const props = defineProps<{ me: User; peer?: User; messages: Message[]; status: ConnectionStatus; error: string }>()
defineEmits<{ send: [content: string]; dismissError: [] }>()
const messageList = ref<HTMLDivElement | null>(null)

function initials(name: string) {
  return name.split(' ').map(part => part[0]).slice(0, 2).join('')
}

function formatTime(timestamp: string) {
  return new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

watch(() => props.messages, async () => {
  await nextTick()
  messageList.value?.scrollTo({ top: messageList.value.scrollHeight, behavior: 'smooth' })
}, { deep: true })
</script>

<template>
  <section class="conversation">
    <header>
      <template v-if="peer"><span class="avatar">{{ initials(peer.displayName) }}</span><div><strong>{{ peer.displayName }}</strong><small>Private conversation</small></div></template>
      <strong v-else>Select a conversation</strong>
    </header>
    <div ref="messageList" class="message-list">
      <div v-if="messages.length === 0" class="empty"><b>Start a conversation</b><span>Your messages with {{ peer?.displayName ?? 'this person' }} will appear here.</span></div>
      <article v-for="message in messages" :key="message.id" :class="{ mine: message.senderId === me.id }"><div>{{ message.content }}</div><time>{{ formatTime(message.sentAt) }}</time></article>
    </div>
    <div v-if="error" class="error" role="alert">{{ error }}<button @click="$emit('dismissError')">×</button></div>
    <MessageComposer :peer="peer" :status="status" @send="$emit('send', $event)" />
  </section>
</template>
