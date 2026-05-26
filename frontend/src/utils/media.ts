export const normalizeMediaUrl = (url?: string) => {
  if (!url) return ''
  const raw = String(url).trim()
  if (!raw) return ''

  if (raw.startsWith('http://') || raw.startsWith('https://')) return raw
  if (raw.startsWith('/upload/')) return raw
  if (raw.startsWith('upload/')) return `/${raw}`

  const normalized = raw.startsWith('/') ? raw : `/${raw}`
  if (
    normalized.startsWith('/avatar/') ||
    normalized.startsWith('/logo/') ||
    normalized.startsWith('/activity/') ||
    normalized.startsWith('/voucher/')
  ) {
    return `/upload${normalized}`
  }
  return normalized
}
