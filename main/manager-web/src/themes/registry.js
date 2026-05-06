/**
 * UI theme registry — add a row here and a matching `html[data-ui-theme="…"]`
 * block in `src/styles/ui-themes.css` to ship a new theme with minimal wiring.
 */
export const UI_THEME_STORAGE_KEY = "manager-web-ui-theme";

export const DEFAULT_UI_THEME_ID = "dark";

/** @typedef {{ id: string, labelKey: string, order?: number }} UiThemeMeta */

/** @type {UiThemeMeta[]} */
export const UI_THEMES = [
  { id: "dark", labelKey: "theme.dark", order: 0 },
  { id: "light", labelKey: "theme.light", order: 1 },
];

export function getRegisteredThemeIds() {
  return UI_THEMES.map((t) => t.id);
}

export function isValidThemeId(id) {
  return typeof id === "string" && getRegisteredThemeIds().includes(id);
}

export function getThemeMeta(id) {
  return UI_THEMES.find((t) => t.id === id) || null;
}

export function getOrderedThemes() {
  return [...UI_THEMES].sort(
    (a, b) => (a.order ?? 0) - (b.order ?? 0),
  );
}
