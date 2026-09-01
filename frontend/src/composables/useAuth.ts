import { onMounted, ref } from 'vue'
import { apiError, apiPath, getJson } from '../api/api'
import type { User } from '../types'

type Csrf = { token: string; headerName: string }
type AuthPath = '/auth/login' | '/auth/register'

export function useAuth() {
  const csrf = ref<Csrf | null>(null)
  const me = ref<User | null>(null)
  const booting = ref(true)
  const error = ref('')

  async function refreshCsrf() {
    const fresh = await getJson<Csrf>('/auth/csrf')
    csrf.value = fresh
    return fresh
  }

  async function restoreSession() {
    try {
      await refreshCsrf()
      const response = await fetch(apiPath('/auth/me'))
      if (response.ok) me.value = await response.json()
    } catch (reason) {
      error.value = reason instanceof Error ? reason.message : 'Could not start application'
    } finally {
      booting.value = false
    }
  }

  async function authenticate(path: AuthPath, payload: object) {
    if (!csrf.value) throw new Error('Security token is not ready')
    const send = (token: Csrf) => fetch(apiPath(path), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', [token.headerName]: token.token },
      body: JSON.stringify(payload),
    })
    let response = await send(csrf.value)
    if (response.status === 403) response = await send(await refreshCsrf())
    if (!response.ok) throw await apiError(response, 'Authentication failed')
    return response.json() as Promise<User>
  }

  async function login(username: string, password: string) {
    me.value = await authenticate('/auth/login', { username, password })
    error.value = ''
  }

  async function register(username: string, displayName: string, password: string) {
    await authenticate('/auth/register', { username, displayName, password })
    await login(username, password)
  }

  async function logout() {
    if (!csrf.value) return
    const response = await fetch(apiPath('/auth/logout'), {
      method: 'POST',
      headers: { [csrf.value.headerName]: csrf.value.token },
    })
    if (!response.ok) {
      error.value = 'Could not sign out'
      return
    }
    me.value = null
    try {
      await refreshCsrf()
    } catch (reason) {
      error.value = reason instanceof Error ? reason.message : 'Could not refresh security token'
    }
  }

  onMounted(restoreSession)
  return { csrf, me, booting, error, login, register, logout }
}
