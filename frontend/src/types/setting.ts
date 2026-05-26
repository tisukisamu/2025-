export interface Setting {
  id: number
  settingKey: string
  settingValue?: string
  description?: string
  createdAt: string
  updatedAt: string
}

export interface SettingRequest {
  settingValue: string
}
