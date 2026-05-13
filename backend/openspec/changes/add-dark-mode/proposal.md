## Why

The online mall frontend currently has a fixed light color scheme. Dark mode is a widely expected feature that reduces eye strain in low-light environments and improves user comfort. Adding it now aligns with modern UX standards and increases user satisfaction across browsing, shopping, and admin workflows.

## What Changes

- Add a dark color palette (backgrounds, text, borders, surfaces) that mirrors the existing light theme structure
- Introduce a theme toggle control in the header accessible from every page
- Persist the user's theme preference in localStorage so it survives page reloads
- Support `prefers-color-scheme` system preference as the default on first visit
- Migrate hardcoded color values in component styles to CSS custom properties so they respond to theme changes
- Enable Element Plus built-in dark mode via the `html.dark` class to style all Element Plus components consistently
- Add dark-mode-aware styles for the main layout shell (header, sidebar, content area)

## Capabilities

### New Capabilities
- `theme-switching`: Theme toggle mechanism, persistence, system-preference detection, and CSS custom property layer that powers dark/light mode across the app

### Modified Capabilities
<!-- No existing specs to modify -->

## Impact

- **Frontend styles**: `src/styles/variables.scss`, `src/styles/index.scss`, and all component `<style>` blocks with hardcoded colors will be touched
- **Frontend layout**: `src/layout/` components (Header, Sidebar, main layout) need dark-mode variants
- **Vuex store**: New `theme` module added to `src/store/`
- **Dependencies**: No new npm dependencies required — Element Plus dark mode is built-in
- **Backend**: No backend changes needed — theme is purely client-side
- **i18n**: One new translation key for the toggle label (zh/en)
