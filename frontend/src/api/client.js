import axios from 'axios'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const orgId = localStorage.getItem('orgId')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  if (orgId) {
    config.headers['X-Org-Id'] = orgId
  }
  return config
})

export default client
