const API = '/api'

export async function apiError(response: Response, fallback: string) {
  try {
    const body = await response.json()
    return new Error(body.message ?? fallback)
  } catch {
    return new Error(fallback)
  }
}

export async function getJson<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API}${path}`, options)
  if (!response.ok) throw await apiError(response, 'Request failed')
  return response.json() as Promise<T>
}

export function apiPath(path: string) {
  return `${API}${path}`
}
