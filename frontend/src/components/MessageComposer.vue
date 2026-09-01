<script setup lang="ts">
import { ref, watch } from 'vue'
import type { ConnectionStatus, User } from '../types'

const props = defineProps<{ peer?: User; status: ConnectionStatus }>()
const emit = defineEmits<{ send: [content: string] }>()
const draft = ref('')

function submit() {
  const content = draft.value.trim()
  if (!content || !props.peer || props.status !== 'online') return
  emit('send', content)
  draft.value = ''
}

watch(() => props.peer?.id, () => { draft.value = '' })
</script>

<template>
  <form class="composer" @submit.prevent="submit">
    <input v-model="draft" aria-label="Message" maxlength="2000" :placeholder="status === 'online' ? `Message ${peer?.displayName ?? ''}` : 'Connecting…'" :disabled="!peer || status !== 'online'">
    <button aria-label="Send message" :disabled="!draft.trim() || status !== 'online'">↑</button>
  </form>
</template>
