## ADDED Requirements

### Requirement: Theme toggle control
The system SHALL provide a theme toggle button in the global header that switches between light and dark mode. The button SHALL display a sun icon in dark mode and a moon icon in light mode.

#### Scenario: User toggles to dark mode
- **WHEN** user clicks the theme toggle button while in light mode
- **THEN** the page background, text colors, and all Element Plus components switch to their dark color variants

#### Scenario: User toggles to light mode
- **WHEN** user clicks the theme toggle button while in dark mode
- **THEN** the page background, text colors, and all Element Plus components switch to their light color variants

### Requirement: Theme persistence
The system SHALL persist the user's theme preference in localStorage under the key `theme`. On subsequent visits, the system SHALL restore the saved theme before Vue mounts to prevent flash of unstyled content.

#### Scenario: Theme survives page reload
- **WHEN** user has selected dark mode and refreshes the page
- **THEN** the page loads in dark mode without a visible flash of light mode

#### Scenario: First visit with no saved preference
- **WHEN** a user visits the site for the first time with no `theme` key in localStorage
- **THEN** the system SHALL read `prefers-color-scheme` from the browser and apply dark mode if the system preference is dark, otherwise light mode

### Requirement: Element Plus dark mode integration
The system SHALL import `element-plus/theme-chalk/dark/css-vars.css` and toggle the `dark` class on the `<html>` element to activate Element Plus built-in dark mode.

#### Scenario: Element Plus components respond to theme switch
- **WHEN** the user toggles to dark mode
- **THEN** all Element Plus components (buttons, inputs, tables, dialogs, menus, etc.) render with their dark color variants automatically

### Requirement: App-level CSS custom properties
The system SHALL define a set of `--app-*` CSS custom properties for backgrounds, text, borders, and surface colors. Light values SHALL be defined on `:root` and dark values SHALL be defined on `html.dark`. All hardcoded color references in app styles SHALL be migrated to use these variables.

#### Scenario: Custom app styles respond to dark mode
- **WHEN** the user toggles to dark mode
- **THEN** the main layout background, sidebar, header, card surfaces, and text colors in custom (non-Element-Plus) styles switch to dark variants via CSS custom properties

#### Scenario: Hardcoded colors are eliminated
- **WHEN** the developer inspects the SCSS and Vue component styles
- **THEN** no hardcoded hex color values remain for backgrounds, text, or borders — all reference `--app-*` variables (except third-party canvas-rendered components like ECharts and AMap)

### Requirement: FOUC prevention
The system SHALL include an inline script in `public/index.html` that reads the `theme` localStorage key and adds the `dark` class to `<html>` before the page renders.

#### Scenario: No flash on dark-to-dark reload
- **WHEN** the user reloads the page while in dark mode
- **THEN** the `<html>` element has the `dark` class before the first paint, and no light-mode flash is visible

### Requirement: Vuex theme state
The system SHALL add a `theme` Vuex module that tracks the current theme as a boolean `isDark` getter. The toggle action SHALL update both the Vuex state and localStorage, and add/remove the `html.dark` class.

#### Scenario: Theme state is reactive
- **WHEN** a component accesses `store.getters.isDark`
- **THEN** it returns `true` in dark mode and `false` in light mode, and reacts to changes

### Requirement: i18n support for toggle label
The system SHALL add translation keys for the theme toggle tooltip in both Chinese (`zh`) and English (`en`) locale files.

#### Scenario: Toggle tooltip follows locale
- **WHEN** the user hovers over the theme toggle with the locale set to Chinese
- **THEN** the tooltip displays the Chinese translation of the theme toggle label
