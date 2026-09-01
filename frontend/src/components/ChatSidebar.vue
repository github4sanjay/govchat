<script setup lang="ts">
import type { ConnectionStatus, User } from '../types'

defineProps<{ me: User; peers: User[]; peerId: string; status: ConnectionStatus }>()
defineEmits<{ selectPeer: [userId: string]; logout: [] }>()

function initials(name: string) {
  return name.split(' ').map(part => part[0]).slice(0, 2).join('')
}
</script>

<template>
  <aside class="sidebar">
    <div class="brand"><span>G</span><div>GovChat<small>Private workspace</small></div></div>
    <div class="signed-in">
      <span class="avatar">{{ initials(me.displayName) }}</span>
      <span>{{ me.displayName }}<small>@{{ me.username }}</small></span>
      <button title="Sign out" aria-label="Sign out" @click="$emit('logout')">↪</button>
    </div>
    <p class="section-label">Messages</p>
    <nav>
      <button v-for="user in peers" :key="user.id" :class="{ active: user.id === peerId }" @click="$emit('selectPeer', user.id)">
        <span class="avatar">{{ initials(user.displayName) }}</span>
        <span>{{ user.displayName }}<small>@{{ user.username }}</small></span>
      </button>
    </nav>
    <div class="connection" :class="status"><i />{{ status }}</div>
  </aside>
</template>
