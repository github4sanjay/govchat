<script setup lang="ts">
import AuthScreen from './components/AuthScreen.vue'
import ChatSidebar from './components/ChatSidebar.vue'
import ConversationView from './components/ConversationView.vue'
import { useAuth } from './composables/useAuth'
import { useMessaging } from './composables/useMessaging'

const { csrf, me, booting, error: authError, login, register, logout } = useAuth()
const { peers, peer, peerId, visibleMessages, status, error: messagingError, sendMessage } = useMessaging(me)
</script>

<template>
  <div v-if="booting" class="splash"><span>G</span><p>Opening GovChat…</p></div>
  <AuthScreen v-else-if="!me" :csrf-ready="Boolean(csrf)" :error="authError" :on-login="login" :on-register="register" />
  <main v-else class="shell">
    <ChatSidebar :me="me" :peers="peers" :peer-id="peerId" :status="status" @select-peer="peerId = $event" @logout="logout" />
    <ConversationView :me="me" :peer="peer" :messages="visibleMessages" :status="status" :error="messagingError"
      @send="sendMessage" @dismiss-error="messagingError = ''" />
  </main>
</template>
