import { request, API_PATHS } from './apiConfig'

// Upload image file
export function uploadFile(data) {
  return request.post(`${API_PATHS.UPLOAD}/image`, data, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
