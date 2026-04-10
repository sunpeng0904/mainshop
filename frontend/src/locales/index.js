import { createI18n } from 'vue-i18n'
import zh from './zh'
import en from './en'

// 从localStorage获取保存的语言设置，默认中文
const savedLang = localStorage.getItem('mall_language') || 'zh'

const i18n = createI18n({
  legacy: false,
  locale: savedLang,
  fallbackLocale: 'zh',
  messages: {
    zh,
    en
  }
})

export default i18n

// 切换语言
export function setLanguage(lang) {
  i18n.global.locale.value = lang
  localStorage.setItem('mall_language', lang)
  document.documentElement.lang = lang
}

// 获取当前语言
export function getLanguage() {
  return i18n.global.locale.value
}
