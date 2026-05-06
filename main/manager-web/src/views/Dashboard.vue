<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t('sidebar.dashboard') }}</h2>
      <div class="right-operations">
        <el-input
          v-model="searchKeyword"
          :placeholder="$t('header.searchPlaceholder')"
          class="search-input"
          clearable
          @keyup.enter.native="triggerSearch"
        />
        <el-button class="btn-search" @click="triggerSearch">
          {{ $t("common.search") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-title">{{ $t("dashboard.agents") }}</div>
              <div class="stat-value">{{ devices.length }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-title">{{ $t("dashboard.users") }}</div>
              <div class="stat-value">{{ summaryUsers }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-title">{{ $t("dashboard.models") }}</div>
              <div class="stat-value">{{ summaryModels }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-title">{{ $t("dashboard.params") }}</div>
              <div class="stat-value">{{ summaryParams }}</div>
            </div>
          </div>

          <div class="hero-card">
            <h2>{{ $t("home.greeting") }}</h2>
            <p>{{ $t("home.wish") }}</p>
            <el-button type="primary" size="mini" class="aurora-btn-primary" @click="showAddDialog">
              {{ $t("home.addAgent") }}
            </el-button>
          </div>

          <div class="device-list-container">
            <template v-if="isLoading">
              <div v-for="i in skeletonCount" :key="'skeleton-' + i" class="skeleton-item">
                <div class="skeleton-image"></div>
                <div class="skeleton-content">
                  <div class="skeleton-line"></div>
                  <div class="skeleton-line-short"></div>
                </div>
              </div>
            </template>
            <template v-else>
              <DeviceItem
                v-for="(item, index) in devices"
                :key="index"
                :device="item"
                :feature-status="featureStatus"
                @configure="goToRoleConfig"
                @deviceManage="handleDeviceManage"
                @delete="handleDeleteAgent"
                @chat-history="handleShowChatHistory"
              />
            </template>
          </div>

        </div>
      </div>
    </div>

    <div class="footer-container">
      <VersionFooter />
    </div>

    <AddWisdomBodyDialog :visible.sync="addDeviceDialogVisible" @confirm="handleWisdomBodyAdded" />
    <chat-history-dialog :visible.sync="showChatHistory" :agent-id="currentAgentId" :agent-name="currentAgentName" />
  </div>
</template>

<script>
import Api from '@/apis/api';
import AddWisdomBodyDialog from '@/components/AddWisdomBodyDialog.vue';
import ChatHistoryDialog from '@/components/ChatHistoryDialog.vue';
import DeviceItem from '@/components/DeviceItem.vue';
import VersionFooter from '@/components/VersionFooter.vue';
import featureManager from '@/utils/featureManager';

export default {
  name: 'Dashboard', // Rebranded from 'HomePage'
  components: { DeviceItem, AddWisdomBodyDialog, VersionFooter, ChatHistoryDialog },
  data() {
    return {
      addDeviceDialogVisible: false,
      devices: [],
      originalDevices: [],
      isSearching: false,
      searchRegex: null,
      isLoading: true,
      skeletonCount: parseInt(localStorage.getItem('skeletonCount') || '8', 10),
      showChatHistory: false,
      currentAgentId: '',
      currentAgentName: '',
      searchKeyword: "",
      summaryModels: "-",
      summaryParams: "-",
      summaryUsers: "-",
      featureStatus: {
        voiceprintRecognition: false,
        voiceClone: false,
        knowledgeBase: false
      }
    }
  },
  async mounted() {
    this.fetchAgentList();
    await this.loadFeatureStatus();
    this.fetchSummaries();
  },
  methods: {
    fetchSummaries() {
      Api.model.getModelList({ modelType: "llm", page: 1, limit: 1 }, ({ data }) => {
        this.summaryModels = data?.data?.total || 0;
      });
      Api.admin.getParamsList({ page: 1, limit: 1 }, ({ data }) => {
        this.summaryParams = data?.data?.total || 0;
      });
      Api.admin.getUserList({ page: 1, limit: 1, mobile: "" }, ({ data }) => {
        if (data.code === 0) {
          this.summaryUsers = data.data.total;
        } else {
          this.summaryUsers = "—";
        }
      });
    },
    triggerSearch() {
      this.handleSearch(this.searchKeyword);
    },
    async loadFeatureStatus() {
      await featureManager.waitForInitialization();
      const config = featureManager.getConfig();
      this.featureStatus = {
        voiceprintRecognition: config.voiceprintRecognition,
        voiceClone: config.voiceClone,
        knowledgeBase: config.knowledgeBase
      };
    },
    showAddDialog() {
      this.addDeviceDialogVisible = true;
    },
    goToRoleConfig() {
      this.$router.push('/role-config');
    },
    handleWisdomBodyAdded(res) {
      this.fetchAgentList();
      this.addDeviceDialogVisible = false;
    },
    handleDeviceManage() {
      this.$router.push('/device-management');
    },
    handleSearch(keyword) {
      this.isSearching = true;
      this.isLoading = true;
      const isMac = /^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$/.test(keyword);
      const searchType = isMac ? 'mac' : 'name';
      Api.agent.searchAgent(keyword, searchType, ({ data }) => {
        if (data?.data) {
          this.devices = data.data.map(item => ({ ...item, agentId: item.id }));
        }
        this.isLoading = false;
      }, (error) => {
        this.isLoading = false;
        this.$message.error(this.$t('message.searchFailed'));
      });
    },
    handleSearchReset() {
      this.isSearching = false;
      this.devices = [...this.originalDevices];
    },
    handleSearchResult(filteredList) {
      this.devices = filteredList;
    },
    fetchAgentList() {
      this.isLoading = true;
      Api.agent.getAgentList(({ data }) => {
        if (data?.data) {
          this.originalDevices = data.data.map(item => ({ ...item, agentId: item.id }));
          this.skeletonCount = Math.min(Math.max(this.originalDevices.length, 3), 10);
          this.handleSearchReset();
        }
        this.isLoading = false;
      }, (error) => {
        this.isLoading = false;
      });
    },
    handleDeleteAgent(agentId) {
      this.$confirm(this.$t('home.confirmDeleteAgent'), 'Prompt', {
        confirmButtonText: this.$t('button.ok'),
        cancelButtonText: this.$t('button.cancel'),
        type: 'warning'
      }).then(() => {
        Api.agent.deleteAgent(agentId, (res) => {
          if (res.data.code === 0) {
            this.$message.success({ message: this.$t('home.deleteSuccess'), showClose: true });
            this.fetchAgentList();
          } else {
            this.$message.error({ message: res.data.msg || this.$t('home.deleteFailed'), showClose: true });
          }
        });
      }).catch(() => { });
    },
    handleShowChatHistory({ agentId, agentName }) {
      this.currentAgentId = agentId;
      this.currentAgentName = agentName;
      this.showChatHistory = true;
    }
  }
}
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
  overflow-y: auto; /* Let the dashboard content scroll */
  background: transparent;
}

.content-area {
  padding: 0 4px; /* Small padding so scrollbar doesn't clip content */
}

/* Custom scrollbar for dashboard */
.content-area::-webkit-scrollbar { width: 6px; }
.content-area::-webkit-scrollbar-thumb {
  background: $border-color;
  border-radius: 10px;
}

/* --- 2. STATS GRID --- */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: $bg-panel;
  border: 1px solid $border-color;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;

  &:hover {
    border-color: rgba(0, 240, 255, 0.3);
    box-shadow: 0 0 15px rgba(0, 240, 255, 0.05);
    transform: translateY(-2px);
  }
}

.stat-title {
  color: $text-muted;
  font-family: $font-mono;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-value {
  margin-top: 12px;
  color: $accent-cyan;
  font-size: 32px;
  font-family: $font-mono;
  font-weight: bold;
}

/* --- 3. HERO CARD --- */
.hero-card {
  background: linear-gradient(135deg, rgba(0, 240, 255, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
  border: 1px solid rgba(0, 240, 255, 0.2);
  border-radius: 10px;
  margin-bottom: 20px;
  padding: 24px;
  position: relative;
  overflow: hidden;

  h2 {
    margin: 0;
    color: $text-main;
    font-size: 24px;
  }
  
  p {
    margin: 10px 0 16px;
    color: $text-muted;
    font-size: 14px;
  }
}

.aurora-btn-primary {
  background: $accent-cyan !important;
  border: none !important;
  color: #000 !important;
  font-weight: bold;
}

/* --- 4. DEVICE LIST --- */
.device-list-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 16px;
  padding-bottom: 20px;
}

/* --- SKELETON LOADER --- */
@keyframes shimmer {
  100% { transform: translateX(100%); }
}

.skeleton-item {
  background: $bg-panel;
  border: 1px solid $border-color;
  border-radius: 10px;
  padding: 20px;
  height: 120px;
  position: relative;
  overflow: hidden;
}

.skeleton-image {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  float: left;
}

.skeleton-content {
  margin-left: 100px;
  padding-top: 10px;
}

.skeleton-line {
  height: 16px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  margin-bottom: 16px;
  width: 70%;
}

.skeleton-line-short {
  height: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  width: 50%;
}

.skeleton-item::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.04), transparent);
  animation: shimmer 1.5s infinite;
}

.footer-container { padding: 10px 0; }
</style>