import React, { createContext, useContext, useState, useEffect } from 'react'
import { authService } from '../services/api'

interface User {
  email: string
  role: string
  plan: string
}

interface AuthContextType {
  user: User | null
  token: string | null
  login: (email: string, password: string) => Promise<void>
  register: (email: string, password: string, firstname?: string, lastname?: string) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'))

  useEffect(() => {
    if (token) {
      // Decode token to get user info (simplified)
      try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        setUser({
          email: payload.sub || payload.email || 'user@example.com',
          role: payload.role || 'USER',
          plan: payload.plan || 'FREE',
        })
      } catch (e) {
        console.error('Error decoding token:', e)
      }
    }
  }, [token])

  const login = async (email: string, password: string) => {
    const response = await authService.login(email, password)
    setToken(response.token)
    localStorage.setItem('token', response.token)
    setUser({
      email,
      role: 'USER',
      plan: 'FREE',
    })
  }

  const register = async (email: string, password: string) => {
    const response = await authService.register(email, password)
    setToken(response.token)
    localStorage.setItem('token', response.token)
    setUser({
      email,
      role: 'USER',
      plan: 'FREE',
    })
  }

  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        register,
        logout,
        isAuthenticated: !!token,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

