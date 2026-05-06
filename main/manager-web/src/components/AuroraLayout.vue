<template>
  <div class="aurora-shell">
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-brand" @click="go('/dashboard')">
        <AuroraLogo />
        <div class="brand-text" v-if="!isCollapsed">
          <span class="brand-title">Aurora Console</span>
          <span class="brand-version">v1.0</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div v-for="group in navGroups" :key="group.id" class="nav-group-container">
          <div 
            :class="group.collapsible ? 'nav-group-header' : 'nav-group-static'"
            @click="group.collapsible ? toggleGroup(group.id) : null"
          >
            <span class="group-title">{{ $t(group.titleKey) }}</span>
            <i v-if="group.collapsible" 
               class="el-icon-arrow-down chevron" 
               :class="{ rotated: openGroups[group.id] }"></i>
          </div>

          <transition name="nav-slide">
            <div v-show="!group.collapsible || openGroups[group.id]" class="nav-links">
              <button
                v-for="item in group.items"
                :key="item.path || item.tab"
                type="button"
                class="nav-link"
                :class="{ active: isLinkActive(item) }"
                @click="handleNavClick(item)"
              >
                <i :class="[item.icon, 'nav-icon']"></i>
                <span v-if="!isCollapsed">{{ $t(item.labelKey) }}</span>
              </button>
            </div>
          </transition>
        </div>
      </nav>

      <div class="sidebar-footer">
        <el-dropdown trigger="click" @command="handleUserCommand" placement="top-start">
          <div class="user-pill">
            <img :src="userAvatar" class="user-avatar" />
            <div class="user-meta" v-if="!isCollapsed">
              <span class="user-name">{{ userName }}</span>
              <span class="user-status">{{ userEmail }}</span>
            </div>
            <i class="el-icon-more user-more" v-if="!isCollapsed"></i>
          </div>
          <el-dropdown-menu slot="dropdown" class="aurora-dropdown">
            <el-dropdown-item command="profile"><i class="el-icon-user"></i> {{ $t('header.profile') }}</el-dropdown-item>
            <el-dropdown-item command="changePassword"><i class="el-icon-lock"></i> {{ $t('header.changePassword') }}</el-dropdown-item>
            <el-dropdown-item command="logout" divided class="logout-item"><i class="el-icon-switch-button"></i> {{ $t('header.logout') }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </aside>

    <main class="main-viewport">
      <div class="content-socket">
        <router-view :key="$route.fullPath"></router-view>
      </div>
    </main>

    <ChangePasswordDialog v-model="passwordVisible" />
  </div>
</template>

<script>
import AuroraLogo from "@/components/AuroraLogo.vue";
import { mapGetters, mapMutations } from 'vuex';
import ChangePasswordDialog from "@/components/ChangePasswordDialog.vue";

const SIDEBAR_STATE_KEY = 'aurora_sidebar_memory';

export default {
  name: "AuroraLayout",
  components: { AuroraLogo, ChangePasswordDialog },
  data() {
    const savedState = localStorage.getItem(SIDEBAR_STATE_KEY);
    const initialGroups = savedState ? JSON.parse(savedState) : { ai: false, resources: false, admin: false };

    return {
      passwordVisible: false,
      isCollapsed: false,
      openGroups: initialGroups,
      navGroups: [
        {
          id: 'ops',
          titleKey: 'navGroups.operations',
          collapsible: false,
          items: [
            { labelKey: 'sidebar.dashboard', path: '/dashboard', icon: 'el-icon-monitor' },
            { labelKey: 'sidebar.deviceManagement', path: '/device-management', icon: 'el-icon-cpu' }
          ]
        },
        {
          id: 'ai',
          titleKey: 'navGroups.ai-capabilities',
          collapsible: true,
          items: [
            { labelKey: 'sidebar.largeLanguageModel', tab: 'llm', icon: 'el-icon-chat-dot-round' },
            { labelKey: 'sidebar.visualLanguageModel', tab: 'vllm', icon: 'el-icon-view' },
            { labelKey: 'sidebar.intentRecognition', tab: 'intent', icon: 'el-icon-aim' },
            { labelKey: 'sidebar.voiceActivityDetection', tab: 'vad', icon: 'el-icon-mic' },
            { labelKey: 'sidebar.speechRecognition', tab: 'asr', icon: 'el-icon-microphone' },
            { labelKey: 'sidebar.textToSpeech', tab: 'tts', icon: 'el-icon-headset' },
            { labelKey: 'sidebar.rag', tab: 'rag', icon: 'el-icon-collection' },
            { labelKey: 'sidebar.memory', tab: 'memory', icon: 'el-icon-data-board' }
          ]
        },
        {
          id: 'resources',
          titleKey: 'navGroups.resources',
          collapsible: true,
          items: [
            { labelKey: 'sidebar.providerManagement', path: '/provider-management', icon: 'el-icon-office-building' },
            { labelKey: 'sidebar.dictManagement', path: '/dict-management', icon: 'el-icon-notebook-2' },
            { labelKey: 'sidebar.agentTemplateManagement', path: '/agent-template-management', icon: 'el-icon-document-copy' },
            { labelKey: 'sidebar.replacementWordManagement', path: '/replacement-word-management', icon: 'el-icon-refresh-left' }
          ]
        },
        {
          id: 'admin',
          titleKey: 'navGroups.administration',
          collapsible: true,
          items: [
            { labelKey: 'sidebar.ParamsManagement', path: '/params-management', icon: 'el-icon-setting' },
            { labelKey: 'sidebar.userManagement', path: '/user-management', icon: 'el-icon-user' },
            { labelKey: 'sidebar.serverManagement', path: '/server-side-management', icon: 'el-icon-set-up' },
            { labelKey: 'sidebar.otaManagement', path: '/ota-management', icon: 'el-icon-upload' },
            { labelKey: 'sidebar.systemFeatureManagement', path: '/feature-management', icon: 'el-icon-data-analysis' },
            { labelKey: 'sidebar.generalSettings', path: '/settings', icon: 'el-icon-setting' }
          ]
        }
      ]
    };
  },
  computed: {
    ...mapGetters(['userInfo']),
    userAvatar() { return this.userInfo?.avatar || require('@/assets/user-avatar1.png'); },
    userName() { return this.userInfo?.name || 'Admin'; },
    userEmail() { return this.userInfo?.email || 'admin@aurora.io'; }
  },
  watch: {
    '$route': {
      handler: 'syncSidebarState',
      immediate: true
    }
  },
  methods: {
    ...mapMutations(['logout']),
    
    toggleGroup(id) {
      this.openGroups[id] = !this.openGroups[id];
      this.persistState();
    },

    persistState() {
      localStorage.setItem(SIDEBAR_STATE_KEY, JSON.stringify(this.openGroups));
    },

    isLinkActive(item) {
      if (item.path) return this.$route.path === item.path;
      if (item.tab) return this.$route.path === '/model-config' && this.$route.query.tab === item.tab;
      return false;
    },

    syncSidebarState() {
      let changed = false;
      this.navGroups.forEach(group => {
        if (!group.collapsible) return;
        const hasActiveChild = group.items.some(item => this.isLinkActive(item));
        
        if (hasActiveChild && !this.openGroups[group.id]) {
          this.openGroups[group.id] = true;
          changed = true;
        }
      });
      if (changed) this.persistState();
    },

    handleNavClick(item) {
      if (item.path) {
        if (this.$route.path !== item.path) this.$router.push(item.path);
      } else if (item.tab) {
        if (this.$route.query.tab !== item.tab) {
          this.$router.push({ path: '/model-config', query: { tab: item.tab } });
        }
      }
    },

    handleUserCommand(command) {
      if (command === 'logout') {
        this.$confirm(this.$t('header.logoutConfirm'), this.$t('header.logout'), { type: 'warning' })
          .then(() => { this.logout(); this.$router.push('/login'); });
      } else if (command === 'changePassword') {
        this.passwordVisible = true;
      }
    },

    go(path) { if (this.$route.path !== path) this.$router.push(path); }
  }
};
</script>

<style scoped lang="scss">
@import "../styles/aurora-theme.scss";

.aurora-shell {
  display: flex;
  height: 100vh;
  width: 100vw;
  background: $bg-base;
  overflow: hidden;
}

.sidebar {
  width: 260px;
  background: $bg-panel;
  border-right: 1px solid $border-color;
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 100;
  &.collapsed { width: 80px; }
}

.sidebar-brand {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  .brand-title { font-size: 16px; color: $accent-cyan; font-family: $font-mono; font-weight: bold; }
  .brand-version { font-size: 10px; color: $accent-purple; opacity: 0.8; }
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
  scrollbar-width: none;
  &::-webkit-scrollbar { display: none; }
}

.nav-group-header, .nav-group-static {
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: $text-muted;
  font-family: $font-mono;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.nav-group-header {
  cursor: pointer;
  transition: color 0.2s;
  &:hover { color: $text-main; background: rgba(255, 255, 255, 0.02); }
  .chevron { transition: transform 0.3s; &.rotated { transform: rotate(180deg); } }
}

.nav-link {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 24px;
  background: transparent;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  text-align: left;
  .nav-icon { font-size: 16px; width: 20px; text-align: center; }
  &:hover { color: $accent-cyan; background: rgba(0, 240, 255, 0.05); }
  &.active {
    color: $accent-cyan;
    background: linear-gradient(90deg, rgba(0, 240, 255, 0.1) 0%, transparent 100%);
    border-left: 3px solid $accent-cyan;
    padding-left: 21px;
  }
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  .user-pill {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 12px;
    border-radius: 8px;
    cursor: pointer;
    background: rgba(255, 255, 255, 0.03);
    &:hover { background: rgba(255, 255, 255, 0.08); }
  }
  .user-avatar { width: 32px; height: 32px; border-radius: 6px; border: 1px solid $border-color; }
  .user-meta { display: flex; flex-direction: column; flex: 1; min-width: 0; }
  .user-name { font-size: 13px; color: $text-main; font-weight: bold; }
  .user-status { font-size: 11px; color: $text-muted; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
}

.main-viewport { flex: 1; display: flex; flex-direction: column; overflow: hidden; position: relative; }
.content-socket { flex: 1; overflow-y: auto; }

.nav-slide-enter-active, .nav-slide-leave-active { transition: max-height 0.3s ease-in-out; overflow: hidden; }
.nav-slide-enter, .nav-slide-leave-to { max-height: 0; }
.nav-slide-enter-to, .nav-slide-leave { max-height: 800px; }
</style>