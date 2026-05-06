<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("settingsHub.title") }}</h2>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <div class="hub-grid">
            
            <section class="hub-card">
              <h3 class="hub-card-title">
                <i class="el-icon-monitor"></i> {{ $t("settingsHub.appearance") }}
              </h3>
              
              <div class="hub-row">
                <span class="hub-label">{{ $t("settingsHub.language") }}</span>
                <el-dropdown trigger="click" @command="changeLang">
                  <el-button size="mini" class="aurora-dropdown-btn">
                    {{ currentLanguageText }} <i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown" class="aurora-dropdown">
                    <el-dropdown-item command="zh_CN">{{ $t("language.zhCN") }}</el-dropdown-item>
                    <el-dropdown-item command="zh_TW">{{ $t("language.zhTW") }}</el-dropdown-item>
                    <el-dropdown-item command="en">{{ $t("language.en") }}</el-dropdown-item>
                    <el-dropdown-item command="de">{{ $t("language.de") }}</el-dropdown-item>
                    <el-dropdown-item command="vi">{{ $t("language.vi") }}</el-dropdown-item>
                    <el-dropdown-item command="pt_BR">{{ $t("language.ptBR") }}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>

              <div class="hub-row">
                <span class="hub-label">{{ $t("theme.appearance") }}</span>
                <el-dropdown trigger="click" @command="setTheme">
                  <el-button size="mini" class="aurora-dropdown-btn">
                    {{ currentThemeLabel }} <i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown" class="aurora-dropdown">
                    <el-dropdown-item v-for="t in themeOptions" :key="t.id" :command="t.id">
                      {{ $t(t.labelKey) }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </section>

            <section class="hub-card">
              <h3 class="hub-card-title">
                <i class="el-icon-user"></i> {{ $t("settingsHub.account") }}
              </h3>
              <div class="hub-actions">
                <el-button size="small" type="primary" class="aurora-btn-primary" @click="openPassword">
                  {{ $t("header.changePassword") }}
                </el-button>
                <el-button size="small" class="aurora-btn-secondary" @click="goUsers">
                  {{ $t("header.userManagement") }}
                </el-button>
                <el-button size="small" type="danger" plain class="aurora-btn-danger" @click="logout">
                  {{ $t("header.logout") }}
                </el-button>
              </div>
            </section>

          </div>
        </div>
      </div>
    </div>

    <ChangePasswordDialog v-model="passwordVisible" />
    
    <div class="footer-container">
      <VersionFooter />
    </div>
  </div>
</template>

<script>
import { mapActions } from "vuex";
import ChangePasswordDialog from "@/components/ChangePasswordDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";
import i18n, { changeLanguage } from "@/i18n";
import { getOrderedThemes } from "@/themes/registry";
import { applyUiTheme, getStoredThemeId } from "@/utils/uiTheme";

export default {
  name: "SettingsHub",
  components: { ChangePasswordDialog, VersionFooter },
  data() {
    return {
      passwordVisible: false,
      currentThemeId: getStoredThemeId(),
    };
  },
  computed: {
    themeOptions() {
      return getOrderedThemes();
    },
    currentThemeLabel() {
      const meta = this.themeOptions.find((t) => t.id === this.currentThemeId);
      return meta ? this.$t(meta.labelKey) : this.$t("theme.appearance");
    },
    currentLanguageText() {
      const lang = i18n.locale || "zh_CN";
      const langMap = {
        'zh_TW': this.$t("language.zhTW"),
        'en': this.$t("language.en"),
        'de': this.$t("language.de"),
        'vi': this.$t("language.vi"),
        'pt_BR': this.$t("language.ptBR")
      };
      return langMap[lang] || this.$t("language.zhCN");
    },
  },
  created() {
    this.currentThemeId = getStoredThemeId();
  },
  methods: {
    ...mapActions(["logout"]),
    changeLang(lang) {
      changeLanguage(lang);
      this.$message.success({ message: this.$t("message.success"), showClose: true });
    },
    setTheme(id) {
      applyUiTheme(id);
      this.currentThemeId = id;
      this.$message.success({ message: this.$t("theme.applied"), showClose: true });
    },
    openPassword() {
      this.passwordVisible = true;
    },
    goUsers() {
      this.$router.push("/user-management");
    },
  },
};
</script>

<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

/* --- 0. PAGE CONTAINER --- */
.page-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.content-panel, .content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background: transparent;
  padding: 24px;
}

/* --- 2. HUB GRID & CARDS --- */
.hub-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  max-width: 1000px;
}

.hub-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid $border-color;
  border-radius: 10px;
  padding: 20px;
  transition: all 0.3s ease;

  &:hover {
    border-color: rgba(0, 240, 255, 0.3);
    box-shadow: 0 0 15px rgba(0, 240, 255, 0.05);
  }
}

.hub-card-title {
  margin: 0 0 20px;
  font-size: 15px;
  font-family: $font-mono;
  color: $accent-cyan;
  display: flex;
  align-items: center;
  gap: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.hub-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);

  &:last-child {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
}

.hub-label {
  color: $text-muted;
  font-size: 13px;
  font-family: $font-mono;
}

/* --- 3. UI ELEMENTS --- */
.aurora-dropdown-btn {
  background: $bg-panel-hover !important;
  border: 1px solid $border-color !important;
  color: $text-main !important;
  font-family: $font-mono;
  min-width: 120px;
  text-align: left;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &:hover {
    border-color: $accent-cyan !important;
  }
}

.hub-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.aurora-btn-primary {
  background: $accent-cyan !important;
  border: none !important;
  color: #000 !important;
  font-weight: bold;
}

.aurora-btn-secondary {
  background: $bg-panel-hover !important;
  border: 1px solid $border-color !important;
  color: $text-main !important;
}

.footer-container {
  padding: 10px 0;
}

/* Custom scrollbar for content area */
.content-area::-webkit-scrollbar { width: 6px; }
.content-area::-webkit-scrollbar-thumb {
  background: $border-color;
  border-radius: 10px;
}
</style>