import api from './index'

// Upload image file
export function uploadFile(data) {
  return api.post('/api/upload/image', data, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
