import {
  UI_THEME_STORAGE_KEY,
  DEFAULT_UI_THEME_ID,
  isValidThemeId,
} from "@/themes/registry";

export function getStoredThemeId() {
  try {
    const raw = localStorage.getItem(UI_THEME_STORAGE_KEY);
    if (raw && isValidThemeId(raw)) {
      return raw;
    }
  } catch {
    /* ignore */
  }
  return DEFAULT_UI_THEME_ID;
}

/**
 * Applies theme to `document.documentElement` and persists choice.
 * @param {string} themeId
 * @returns {string} resolved id
 */
export function applyUiTheme(themeId) {
  const id = isValidThemeId(themeId) ? themeId : DEFAULT_UI_THEME_ID;
  if (typeof document !== "undefined") {
    document.documentElement.setAttribute("data-ui-theme", id);
  }
  try {
    localStorage.setItem(UI_THEME_STORAGE_KEY, id);
  } catch {
    /* ignore */
  }
  return id;
}

/** Call once at app bootstrap (before paint if possible). */
export function initUiTheme() {
  applyUiTheme(getStoredThemeId());
}
