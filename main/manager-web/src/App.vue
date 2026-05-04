<template>
  <div id="app">
    <router-view :key="$i18n.locale" />
    <cache-viewer v-if="isCDNEnabled" :visible.sync="showCacheViewer" />
  </div>
</template>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}

.copyright {
  padding: 0 !important;
  color: rgb(0, 0, 0);
  font-size: 12px;
  font-weight: 400;
  margin-top: auto;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-message {
  top: 70px !important;
}
</style>

<script>
import CacheViewer from "@/components/CacheViewer.vue";
import { logCacheStatus } from "@/utils/cacheViewer";

// 1. Import Element UI Locale manager and language packs
import locale from 'element-ui/lib/locale';
import en from 'element-ui/lib/locale/lang/en';
import zhCn from 'element-ui/lib/locale/lang/zh-CN';
import zhTw from 'element-ui/lib/locale/lang/zh-TW';
import ja from 'element-ui/lib/locale/lang/ja';
import vi from 'element-ui/lib/locale/lang/vi';

export default {
  name: "App",
  components: {
    CacheViewer,
  },
  data() {
    return {
      showCacheViewer: false,
      isCDNEnabled: process.env.VUE_APP_USE_CDN === "true",
      // Map your i18n codes to Element UI language packs
      localeMap: {
        'en': en,
        'zh-CN': zhCn,
        'zh-TW': zhTw,
        'ja': ja,
        'vi': vi
      }
    };
  },
  watch: {
    // 2. Watch for i18n language changes and update Element UI globally
    '$i18n.locale': {
      handler(newLocale) {
        // Change Element UI's internal language
        locale.use(this.localeMap[newLocale] || en);
      },
      immediate: true // Run immediately on startup
    }
  },
  created() {
    // Mount store state
    this.$store.commit(
      "setUserInfo",
      JSON.parse(localStorage.getItem("userInfo") || "{}"),
    );
    this.$store.commit(
      "setPubConfig",
      JSON.parse(localStorage.getItem("pubConfig") || "{}"),
    );
  },
  mounted() {
    // Check if it is a mobile device and VUE_APP_H5_URL is not empty, if both conditions are met, jump to the H5 page
    if (this.isMobileDevice() && process.env.VUE_APP_H5_URL) {
      window.location.href = process.env.VUE_APP_H5_URL;
      return;
    }

    // Only add related events and functions when CDN is enabled
    if (this.isCDNEnabled) {
      // Add global shortcut Alt+C to show cache viewer
      document.addEventListener("keydown", this.handleKeyDown);

      // Add cache inspection method to the global object for debugging
      window.checkCDNCacheStatus = () => {
        this.showCacheViewer = true;
      };

      // Output prompt information in the console
      console.info(
        "%c[" + this.$t("system.name") + "] " + this.$t("cache.cdnEnabled"),
        "color: #409EFF; font-weight: bold;",
      );
      console.info(
        "Press Alt+C combination key or run checkCDNCacheStatus() in the console to view the CDN cache status",
      );

      // Check Service Worker status
      this.checkServiceWorkerStatus();
    } else {
      console.info(
        "%c[" + this.$t("system.name") + "] " + this.$t("cache.cdnDisabled"),
        "color: #67C23A; font-weight: bold;",
      );
    }
  },
  beforeDestroy() {
    // Only need to remove event listeners when CDN is enabled
    if (this.isCDNEnabled) {
      document.removeEventListener("keydown", this.handleKeyDown);
    }
  },
  methods: {
    handleKeyDown(e) {
      // Alt+C shortcut key
      if (e.altKey && e.key === "c") {
        this.showCacheViewer = true;
      }
    },
    isMobileDevice() {
      // Function to check if it is a mobile device
      return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
        navigator.userAgent,
      );
    },

    async checkServiceWorkerStatus() {
      // Check if Service Worker is registered
      if ("serviceWorker" in navigator) {
        try {
          const registrations =
            await navigator.serviceWorker.getRegistrations();
          if (registrations.length > 0) {
            console.info(
              "%c[" +
                this.$t("system.name") +
                "] " +
                this.$t("cache.serviceWorkerRegistered"),
              "color: #67C23A; font-weight: bold;",
            );

            // Output cache status to the console
            setTimeout(async () => {
              const hasCaches = await logCacheStatus();
              if (!hasCaches) {
                console.info(
                  "%c[" +
                    this.$t("system.name") +
                    "] " +
                    this.$t("cache.noCacheDetected"),
                  "color: #E6A23C; font-weight: bold;",
                );

                // Provide extra tips in development environment
                if (process.env.NODE_ENV === "development") {
                  console.info(
                    "%c[" +
                      this.$t("system.name") +
                      "] " +
                      this.$t("cache.swDevEnvWarning"),
                    "color: #E6A23C; font-weight: bold;",
                  );
                  console.info(this.$t("cache.swCheckMethods"));
                  console.info("1. " + this.$t("cache.swCheckMethod1"));
                  console.info("2. " + this.$t("cache.swCheckMethod2"));
                  console.info("3. " + this.$t("cache.swCheckMethod3"));
                }
              }
            }, 2000);
          } else {
            console.info(
              "%c[" +
                this.$t("system.name") +
                "] " +
                this.$t("cache.serviceWorkerNotRegistered"),
              "color: #F56C6C; font-weight: bold;",
            );

            if (process.env.NODE_ENV === "development") {
              console.info(
                "%c[" +
                  this.$t("system.name") +
                  "] " +
                  this.$t("cache.swDevEnvNormal"),
                "color: #E6A23C; font-weight: bold;",
              );
              console.info(this.$t("cache.swProdOnly"));
              console.info(this.$t("cache.swTestingTitle"));
              console.info("1. " + this.$t("cache.swTestingStep1"));
              console.info("2. " + this.$t("cache.swTestingStep2"));
            }
          }
        } catch (error) {
          console.error("Check Service Worker status failed:", error);
        }
      } else {
        console.warn(this.$t("cache.swNotSupported"));
      }
    }
  }
};
</script>