import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons(),
  ],
  shortcuts: {
    // 布局类
    'flex-center': 'flex items-center justify-center',
    'flex-between': 'flex items-center justify-between',
    'flex-col-center': 'flex flex-col items-center justify-center',
    
    // 容器类
    'container-page': 'min-h-screen bg-gray-50', // 淡灰色背景
    'container-content': 'max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8',
    'container-narrow': 'max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8',
    
    // 卡片类 - 现代化简约风格
    'card-modern': 'bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden transition-all duration-300 hover:shadow-md',
    'card-hover': 'hover:-translate-y-1',
    'card-gradient': 'bg-gradient-to-br from-white to-green-50 rounded-2xl shadow-sm border border-gray-100',
    
    // 按钮类 - 淡绿色主调
    'btn-primary': 'px-6 py-2.5 bg-emerald-500 text-white rounded-xl font-medium shadow-sm hover:bg-emerald-600 hover:shadow-md active:scale-95 transition-all duration-300',
    'btn-secondary': 'px-6 py-2.5 bg-white text-emerald-600 border border-emerald-200 rounded-xl font-medium shadow-sm hover:bg-emerald-50 hover:border-emerald-300 transition-all duration-300',
    'btn-success': 'px-6 py-2.5 bg-green-500 text-white rounded-xl font-medium shadow-sm hover:bg-green-600 transition-all duration-300',
    'btn-danger': 'px-6 py-2.5 bg-rose-500 text-white rounded-xl font-medium shadow-sm hover:bg-rose-600 transition-all duration-300',
    'btn-ghost': 'px-6 py-2.5 text-gray-600 rounded-xl font-medium hover:bg-gray-100 transition-all duration-300',
    
    // 输入框类
    'input-modern': 'w-full px-4 py-3 bg-white border border-gray-200 rounded-xl text-gray-700 placeholder-gray-400 transition-all duration-300 focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500/20 outline-none',
    
    // 文本类
    'text-gradient': 'bg-gradient-to-r from-emerald-600 to-green-500 bg-clip-text text-transparent',
    'text-title': 'text-2xl font-bold text-gray-800 tracking-tight',
    'text-subtitle': 'text-lg font-medium text-gray-600',
    'text-body': 'text-base text-gray-600 leading-relaxed',
    'text-caption': 'text-sm text-gray-500',
    'text-primary': 'text-emerald-600',
    
    // 标签类
    'tag-modern': 'px-3 py-1 rounded-full text-sm font-medium',
    'tag-primary': 'bg-emerald-50 text-emerald-600',
    
    // 图片类
    'img-cover': 'w-full h-full object-cover',
    'img-contain': 'w-full h-full object-contain',
    
    // 动画类
    'animate-fade-in': 'animate-fade-in-up',
    
    // 玻璃态效果
    'glass': 'bg-white/80 backdrop-blur-md border border-white/20',
    
    // 统计卡片
    'stat-card': 'card-modern p-6',
    'stat-icon': 'w-12 h-12 rounded-xl flex-center text-2xl',
    
    // 表格样式
    'table-modern': 'w-full border-collapse',
    'table-header': 'bg-gray-50/50 text-gray-500 font-medium text-sm text-left px-6 py-3',
    'table-row': 'border-b border-gray-50 hover:bg-gray-50/30 transition-colors',
    'table-cell': 'px-6 py-4 text-sm text-gray-600',
    
    // 导航栏
    'navbar-modern': 'glass sticky top-0 z-50 shadow-sm border-b border-gray-100/50',
    
    // 阴影
    'shadow-soft': 'shadow-[0_4px_20px_-4px_rgba(0,0,0,0.05)]',
  },
  theme: {
    colors: {
      primary: {
        50: '#ecfdf5',
        100: '#d1fae5',
        200: '#a7f3d0',
        300: '#6ee7b7',
        400: '#34d399',
        500: '#10b981', // Emerald 500
        600: '#059669',
        700: '#047857',
        800: '#065f46',
        900: '#064e3b',
      },
      // 补充淡绿色系
      pale: {
        50: '#f0fdf4',
        100: '#dcfce7',
        200: '#bbf7d0',
        300: '#86efac',
        400: '#4ade80',
        500: '#22c55e',
        600: '#16a34a',
        700: '#15803d',
        800: '#166534',
        900: '#14532d',
      }
    },
  },
})
