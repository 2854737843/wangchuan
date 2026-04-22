import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    orgId: localStorage.getItem('orgId') || '',
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setOrgId(orgId) {
      this.orgId = orgId
      localStorage.setItem('orgId', orgId)
    },
    logout() {
      this.token = ''
      this.orgId = ''
      localStorage.removeItem('token')
      localStorage.removeItem('orgId')
    },
  },
})
