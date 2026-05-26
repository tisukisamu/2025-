const DEFAULT_AVATAR = '/default-avatar.svg'

export const resolveMediaUrl = (path?: string | null) => {
  if (!path) return undefined
  const value = String(path).trim()
  if (!value) return undefined
  if (
    value.startsWith('http://') ||
    value.startsWith('https://') ||
    value.startsWith('data:') ||
    value.startsWith('blob:')
  ) {
    return value
  }
  if (value.startsWith('//')) {
    return `${window.location.protocol}${value}`
  }
  if (value.startsWith('/')) {
    return value
  }
  return `/${value}`
}

export const resolveAvatarUrl = (path?: string | null) => {
  return resolveMediaUrl(path) || DEFAULT_AVATAR
}
