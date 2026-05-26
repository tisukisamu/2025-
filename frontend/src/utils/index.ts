const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

export function getImageUrl(path: string | undefined | null): string {
  if (!path) {
    return ''
  }
  
  const normalized = String(path).trim().replace(/\\/g, '/')
  if (!normalized) {
    return ''
  }
  
  if (normalized.startsWith('http://') || normalized.startsWith('https://')) {
    return normalized
  }
  
  if (normalized.startsWith('/upload')) {
    if (API_BASE_URL) {
      return API_BASE_URL + normalized
    }
    return normalized
  }
  
  if (normalized.startsWith('upload')) {
    if (API_BASE_URL) {
      return API_BASE_URL + '/' + normalized
    }
    return '/' + normalized
  }
  
  if (normalized.startsWith('/api')) {
    if (API_BASE_URL) {
      return API_BASE_URL + normalized
    }
    return normalized
  }
  
  if (normalized.startsWith('api')) {
    if (API_BASE_URL) {
      return API_BASE_URL + '/' + normalized
    }
    return '/' + normalized
  }
  
  return normalized
}

export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export function formatDate(date: string | Date | null | undefined): string {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

export function formatDateTime(date: string | Date | null | undefined): string {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
