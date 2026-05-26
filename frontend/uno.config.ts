import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons()
  ],
  theme: {
    colors: {
      primary: '#1a1a1a',
      secondary: '#4a4a4a',
      accent: '#666666',
      border: '#e5e5e5',
      bg: '#fafafa',
      'bg-dark': '#1a1a1a'
    }
  },
  shortcuts: {
    'flex-center': 'flex items-center justify-center',
    'flex-between': 'flex items-center justify-between',
    'card-base': 'bg-white rounded-lg shadow-sm border border-border'
  }
})
