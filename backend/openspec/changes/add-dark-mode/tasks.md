## 1. Foundation — CSS Custom Properties & Dark Imports

- [ ] 1.1 Import `element-plus/theme-chalk/dark/css-vars.css` in `main.js`
- [ ] 1.2 Define `--app-*` CSS custom properties for light theme on `:root` in `variables.scss` (backgrounds, text, borders, surfaces)
- [ ] 1.3 Define dark overrides for `--app-*` variables under `html.dark` selector in `variables.scss`
- [ ] 1.4 Add FOUC-prevention inline script to `public/index.html` that reads `theme` from localStorage and adds `dark` class to `<html>` before first paint

## 2. Vuex Theme Module

- [ ] 2.1 Create `src/store/modules/theme.js` with `isDark` state, `toggleTheme` mutation, and `initTheme` action (reads localStorage → falls back to `prefers-color-scheme`)
- [ ] 2.2 Register the theme module in `src/store/index.js`
- [ ] 2.3 Dispatch `initTheme` in `main.js` app initialization before mount

## 3. Theme Toggle UI

- [ ] 3.1 Add sun/moon icon toggle button to `Header.vue` next to existing utility controls
- [ ] 3.2 Wire toggle button click to Vuex `toggleTheme` action
- [ ] 3.3 Add tooltip with i18n translation key `header.theme` to `src/locales/zh.js` and `en.js`

## 4. Layout & Component Style Migration

- [ ] 4.1 Migrate `src/layout/index.vue` hardcoded colors to `--app-*` CSS custom properties
- [ ] 4.2 Migrate `src/layout/components/Header.vue` hardcoded colors to `--app-*` CSS custom properties
- [ ] 4.3 Migrate `src/layout/components/Sidebar.vue` (or equivalent) hardcoded colors to `--app-*` CSS custom properties
- [ ] 4.4 Migrate `src/styles/index.scss` hardcoded colors and Element Plus overrides to `--app-*` CSS custom properties
- [ ] 4.5 Scan all remaining `.vue` files for hardcoded hex colors and migrate app-level ones to `--app-*` variables (skip third-party canvas components like ECharts/AMap)

## 5. Verification

- [ ] 5.1 Manual test: toggle between light and dark mode on homepage, product list, cart, order, admin dashboard, share page
- [ ] 5.2 Manual test: reload page in dark mode — verify no FOUC
- [ ] 5.3 Manual test: first visit with system dark preference — verify dark mode applied automatically
- [ ] 5.4 Manual test: switch language (zh/en) — verify toggle tooltip updates
