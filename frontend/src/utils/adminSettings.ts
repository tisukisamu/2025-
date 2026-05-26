export type ThemeMode = 'light' | 'dark' | 'system'

export interface AdminSettings {
  themeMode: ThemeMode
  defaultCollapsed: boolean
  showBreadcrumb: boolean
  showGlobalSearch: boolean
}

const KEY = 'admin_settings_v1'

export const defaultAdminSettings: AdminSettings = {
  themeMode: 'light',
  defaultCollapsed: false,
  showBreadcrumb: true,
  showGlobalSearch: true,
}

export function loadAdminSettings(): AdminSettings {
  try {
    const raw = localStorage.getItem(KEY)
    if (!raw) return defaultAdminSettings
    return { ...defaultAdminSettings, ...JSON.parse(raw) }
  } catch {
    return defaultAdminSettings
  }
}

export function saveAdminSettings(next: AdminSettings) {
  localStorage.setItem(KEY, JSON.stringify(next))
}
