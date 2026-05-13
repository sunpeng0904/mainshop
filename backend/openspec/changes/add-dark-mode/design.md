## Context

The online mall frontend is a Vue 3 + Element Plus 2.4 SPA using SCSS for styling. Colors are currently defined as SCSS variables in `variables.scss` and hardcoded hex values throughout component styles. There is no theming infrastructure — no CSS custom properties, no `data-theme` attribute, no `html.dark` class toggle.

Element Plus 2.4 ships with built-in dark mode support via the `html.dark` class, which flips all `--el-*` CSS variables automatically. This provides a strong foundation — but the app's own custom styles (layout, cards, text) still need explicit dark overrides.

## Goals / Non-Goals

**Goals:**
- Provide a user-facing light/dark toggle that persists across sessions
- Respect `prefers-color-scheme` as the initial default for first-time visitors
- Leverage Element Plus's built-in dark mode for all `el-*` components
- Migrate app-level hardcoded colors to CSS custom properties so they theme-switch automatically
- Keep the change scoped to the frontend — no backend changes

**Non-Goals:**
- Per-component theme customization (e.g., "dark sidebar + light content") — not needed
- High-contrast or accessibility themes — out of scope for this iteration
- Server-side theme preference storage — localStorage is sufficient
- Animated transitions between themes — instant switch is acceptable

## Decisions

### 1. CSS custom properties as the theming layer

**Decision**: Define a set of `--app-*` CSS custom properties on `:root` (light) and `html.dark` (dark), and migrate hardcoded colors in components to reference these variables.

**Why**: CSS custom properties are natively dynamic — changing the `html` class instantly flips all dependent styles without recompilation. SCSS variables are compile-time only and cannot respond to runtime state. Element Plus already uses this pattern (`--el-*` vars), so we stay consistent.

**Alternatives considered**:
- *SCSS mixin approach*: Generate two CSS bundles at build time. Rejected — doubles CSS payload and doesn't allow runtime switching.
- *Inline styles via JS*: Set colors via JavaScript. Rejected — unmaintainable, breaks scoped styles.

### 2. Vuex theme module + localStorage persistence

**Decision**: Add a `theme` Vuex module with a `dark` boolean. On app init, read from localStorage; if absent, read `prefers-color-scheme`. Toggle writes to both Vuex and localStorage, and adds/removes `html.dark`.

**Why**: Vuex provides reactive state across components. `vuex-persistedstate` already handles other modules, but theme is simple enough to use raw `localStorage` directly (no need for the plugin overhead). The `html.dark` class is the single source of truth for CSS — Vuex just tracks the logical state.

**Alternatives considered**:
- *Pinia*: Project uses Vuex 4, not Pinia. Migrating state management for one module is out of scope.
- *CSS-only with `prefers-color-scheme`*: No manual toggle possible. Users expect explicit control.

### 3. Theme toggle in the Header component

**Decision**: Place a sun/moon icon toggle button in the existing `Header.vue`, next to the language switcher and user avatar.

**Why**: The header is visible on every page. Placing the toggle here ensures global accessibility without adding new UI chrome. The existing header already has a row of utility controls — adding one more icon button is minimal visual impact.

### 4. Migrate only app-level colors; rely on Element Plus for component colors

**Decision**: Define `--app-*` variables for backgrounds, text, borders, and surfaces used in custom styles. Do NOT manually override `--el-*` variables — let Element Plus handle its own dark mode.

**Why**: Element Plus dark mode is well-tested. Overriding its variables risks visual bugs on version upgrades. Our custom `--app-*` layer stays independent and maintainable.

## Risks / Trade-offs

- **[Risk] Hardcoded colors missed during migration** → Mitigation: Grep for hex color literals in `.vue` files and `*.scss` files. Accept that some edge cases (e.g., chart colors in ECharts, map styles in AMap) will stay light — these are third-party rendered canvases, not DOM elements.
- **[Risk] Element Plus dark mode CSS not loading** → Mitigation: Verify that `element-plus/theme-chalk/dark/css-vars.css` is imported in `main.js`. This is a one-line import.
- **[Risk] FOUC (flash of unstyled content) on page load** → Mitigation: Read localStorage and set `html.dark` synchronously in `index.html` `<head>` before Vue mounts. Add a small inline `<script>` to `public/index.html`.
- **[Trade-off] Third-party components (ECharts, AMap) stay in light mode** → Acceptable. These render to `<canvas>` and don't respond to CSS variables. Dark themes for charts would require ECharts theme configuration — a separate enhancement.
