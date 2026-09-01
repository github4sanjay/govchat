<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  csrfReady: boolean
  error: string
  onLogin: (username: string, password: string) => Promise<void>
  onRegister: (username: string, displayName: string, password: string) => Promise<void>
}>()

const mode = ref<'login' | 'register'>('login')
const username = ref('')
const displayName = ref('')
const password = ref('')
const formError = ref('')
const submitting = ref(false)

async function submit() {
  submitting.value = true
  formError.value = ''
  try {
    if (mode.value === 'login') await props.onLogin(username.value, password.value)
    else await props.onRegister(username.value, displayName.value, password.value)
  } catch (reason) {
    formError.value = reason instanceof Error ? reason.message : 'Authentication failed'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-card">
      <div class="auth-brand"><span>G</span><div>GovChat<small>Local private messaging</small></div></div>
      <div class="auth-tabs">
        <button :class="{ active: mode === 'login' }" @click="mode = 'login'">Sign in</button>
        <button :class="{ active: mode === 'register' }" @click="mode = 'register'">Create account</button>
      </div>
      <h1>{{ mode === 'login' ? 'Welcome back' : 'Join the conversation' }}</h1>
      <p>{{ mode === 'login' ? 'Sign in to continue to your messages.' : 'Your account stays on this local environment.' }}</p>
      <form class="auth-form" @submit.prevent="submit">
        <label v-if="mode === 'register'">Display name<input v-model="displayName" required minlength="2" maxlength="100" placeholder="Enter your display name"></label>
        <label>Username<input v-model="username" required minlength="3" maxlength="50" autocomplete="username" placeholder="Enter your username"></label>
        <label>Password<input v-model="password" required minlength="8" maxlength="72" type="password" :autocomplete="mode === 'login' ? 'current-password' : 'new-password'" placeholder="Enter your password"></label>
        <div v-if="formError || error" class="auth-error" role="alert">{{ formError || error }}</div>
        <button :disabled="!csrfReady || submitting">{{ submitting ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account' }}</button>
      </form>
    </section>
    <aside class="auth-story"><span>Private by design</span><h2>Messages delivered in real time. Identity verified on every request.</h2><p>Your session authenticates both HTTP history and the WebSocket connection.</p></aside>
  </main>
</template>
