import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface User {
  id: string
  email: string
  full_name: string
  role: string
  autonomy_mode: string
}

interface AuthStore {
  user: User | null
  accessToken: string | null
  refreshToken: string | null
  login: (email: string, password: string) => Promise<void>
  register: (email: string, password: string, full_name: string) => Promise<void>
  logout: () => void
  setUser: (user: User | null) => void
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      user: null,
      accessToken: null,
      refreshToken: null,

      login: async (email: string, password: string) => {
        try {
          const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
          })
          if (!response.ok) throw new Error('Login failed')

          const data = await response.json()
          set({
            accessToken: data.access_token,
            refreshToken: data.refresh_token,
          })
        } catch (error) {
          console.error('Login error:', error)
          throw error
        }
      },

      register: async (email: string, password: string, full_name: string) => {
        try {
          const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password, full_name }),
          })
          if (!response.ok) throw new Error('Registration failed')

          const data = await response.json()
          set({
            accessToken: data.access_token,
            refreshToken: data.refresh_token,
          })
        } catch (error) {
          console.error('Registration error:', error)
          throw error
        }
      },

      logout: () => {
        set({ user: null, accessToken: null, refreshToken: null })
      },

      setUser: (user) => set({ user }),
    }),
    {
      name: 'aegis-auth',
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
      }),
    }
  )
)
