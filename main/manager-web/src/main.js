// 1. Reset and Base Library Styles (Lowest Priority)
import "normalize.css/normalize.css"; 
import "element-ui/lib/theme-chalk/index.css";

// 2. Theme Variables & Initialization (Must happen before Vue renders)
import "./styles/ui-themes.css";
import { initUiTheme } from "./utils/uiTheme";
initUiTheme();

// 3. Core Vue & Plugins
import Vue from "vue";
import ElementUI from "element-ui";
import i18n from "./i18n";
import store from "./store";
import router from "./router";
import App from "./App.vue";

// 4. Global Custom Styles (Highest Priority - Overrides Element UI)
import "./styles/global.scss";
import "./styles/aurora-element-shell.scss";
import "./styles/aurora-element-portal.scss";
import "@/styles/aurora-layout.scss"; // Your new global layout file

// 5. Utilities & Services
import { register as registerServiceWorker } from "./registerServiceWorker";
// Note: featureManager is imported here; ensure it self-initializes or is used in components
import featureManager from "./utils/featureManager"; 

// --- Configuration ---

// Create event bus for legacy inter-component communication
Vue.prototype.$eventBus = new Vue();

Vue.use(ElementUI);
Vue.config.productionTip = false;

// Register Service Worker for PWA/Cache capabilities
registerServiceWorker();

// 6. Mount the App
new Vue({
    router,
    store,
    i18n,
    render: (h) => h(App),
}).$mount("#app");