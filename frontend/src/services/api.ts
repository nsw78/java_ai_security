import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authService = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/authenticate', { email, password })
    return response.data
  },
  register: async (email: string, password: string, firstname?: string, lastname?: string) => {
    const response = await api.post('/auth/register', {
      email,
      password,
      firstname: firstname || 'User',
      lastname: lastname || 'User',
      role: 'USER',
      plan: 'FREE',
    })
    return response.data
  },
}

export const promptService = {
  analyze: async (prompt: string, model: string = 'gpt-4', policy: string = 'moderate') => {
    const response = await api.post('/secure-prompt', {
      prompt,
      model,
      policy,
    })
    return response.data
  },
}

export const auditService = {
  getLogs: async (page: number = 0, size: number = 20) => {
    const response = await api.get('/audit/logs', {
      params: { page, size },
    })
    return response.data
  },
  getStats: async () => {
    const response = await api.get('/audit/stats')
    return response.data
  },
}

export default api

