<template>
  <div class="page-container">
    <div class="operation-bar">
      <h2 class="page-title">{{ $t("header.featureManagement") }}</h2>
      <div class="right-operations">
        <el-button
          @click="!isSaving && toggleSelectAll()"
          class="btn-select-all"
          size="mini"
          :disabled="isSaving"
        >
          {{ isAllSelected ? $t("featureManagement.deselectAll") : $t("featureManagement.selectAll") }}
        </el-button>
        <el-button
          type="success"
          class="save-btn"
          size="mini"
          @click="handleSave"
          :disabled="isSaving"
        >
          {{ isSaving ? $t("featureManagement.saving") : $t("featureManagement.save") }}
        </el-button>
        <el-button
          class="reset-btn"
          size="mini"
          @click="handleReset"
          :disabled="isSaving"
        >
          {{ $t("featureManagement.reset") }}
        </el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="feature-card" shadow="never">
            
            <div class="feature-groups-container">
              <div v-if="featureManagementFeatures.length > 0" class="feature-group">
                <h3 class="group-title">
                  <i class="el-icon-cpu"></i> {{ $t("featureManagement.group.featureManagement") }}
                </h3>
                <div class="features-grid">
                  <div
                    v-for="feature in featureManagementFeatures"
                    :key="feature.id"
                    class="feature-card-item"
                    :class="{ 'feature-enabled': feature.enabled, 'is-loading': isSaving }"
                    @click="!isSaving && toggleFeature(feature)"
                  >
                    <div class="feature-header">
                      <h3 class="feature-name">{{ $t(`feature.${feature.id}.name`) }}</h3>
                      <el-switch
                        v-model="feature.enabled"
                        class="aurora-switch"
                        :disabled="isSaving"
                        @click.native.stop
                        @change="toggleFeature(feature)"
                      />
                    </div>
                    <p class="feature-description">{{ $t(`feature.${feature.id}.description`) }}</p>
                  </div>
                </div>
              </div>

              <div v-if="voiceManagementFeatures.length > 0" class="feature-group">
                <h3 class="group-title">
                  <i class="el-icon-microphone"></i> {{ $t("featureManagement.group.voiceManagement") }}
                </h3>
                <div class="features-grid">
                  <div
                    v-for="feature in voiceManagementFeatures"
                    :key="feature.id"
                    class="feature-card-item"
                    :class="{ 'feature-enabled': feature.enabled, 'is-loading': isSaving }"
                    @click="!isSaving && toggleFeature(feature)"
                  >
                    <div class="feature-header">
                      <h3 class="feature-name">{{ $t(`feature.${feature.id}.name`) }}</h3>
                      <el-switch
                        v-model="feature.enabled"
                        class="aurora-switch"
                        :disabled="isSaving"
                        @click.native.stop
                        @change="toggleFeature(feature)"
                      />
                    </div>
                    <p class="feature-description">{{ $t(`feature.${feature.id}.description`) }}</p>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="filteredFeatures.length === 0" class="empty-state">
              <el-empty :description="$t('featureManagement.noFeatures')" />
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <div class="footer-container">
      <VersionFooter />
    </div>
  </div>
</template>

<script>
import VersionFooter from "@/components/VersionFooter.vue";
import featureManager from "@/utils/featureManager.js";

export default {
  name: "FeatureManagement",
  components: { VersionFooter },
  data() {
    return {
      pendingChanges: false,
      featureManagementFeatures: [],
      voiceManagementFeatures: [],
      isSaving: false,
    };
  },
  computed: {
    filteredFeatures() {
      return [...this.featureManagementFeatures, ...this.voiceManagementFeatures];
    },
    isAllSelected() {
      const all = this.filteredFeatures;
      return all.length > 0 && all.every((f) => f.enabled);
    },
  },
  async created() {
    try {
      await featureManager.waitForInitialization();
      await this.loadFeatures();
      this.setupConfigChangeListener();
    } catch (e) {
      await this.loadFeatures();
    }
  },
  beforeDestroy() {
    this.removeConfigChangeListener();
  },
  methods: {
    async getFeaturesByIds(ids) {
      const config = await featureManager.getAllFeatures();
      return ids.map(id => ({
        id,
        enabled: config[id]?.enabled || false
      }));
    },
    async loadFeatures() {
      this.featureManagementFeatures = await this.getFeaturesByIds([
        "voiceprintRecognition", "voiceClone", "knowledgeBase", "mcpAccessPoint"
      ]);
      this.voiceManagementFeatures = await this.getFeaturesByIds(["vad", "asr"]);
    },
    toggleFeature(feature) {
      if (this.isSaving) return;
      feature.enabled = !feature.enabled;
      this.pendingChanges = true;
    },
    async handleSave() {
      if (!this.pendingChanges) return this.$message.info(this.$t("featureManagement.noChanges"));
      this.isSaving = true;
      try {
        const updates = {};
        this.filteredFeatures.forEach(f => updates[f.id] = f.enabled);
        await featureManager.updateFeatures(updates);
        this.pendingChanges = false;
        this.$message.success(this.$t("featureManagement.saveSuccess"));
        setTimeout(() => this.loadFeatures(), 500);
      } catch (e) {
        this.$message.error(this.$t("featureManagement.saveError"));
      } finally {
        this.isSaving = false;
      }
    },
    setupConfigChangeListener() {
      this.configChangeHandler = () => this.loadFeatures();
      window.addEventListener("featureConfigReloaded", this.configChangeHandler);
    },
    removeConfigChangeListener() {
      window.removeEventListener("featureConfigReloaded", this.configChangeHandler);
    },
    async handleReset() {
      this.$confirm(this.$t("featureManagement.resetConfirm"), "Reset", { type: 'warning' }).then(() => {
        featureManager.resetToDefault();
        this.loadFeatures();
        this.pendingChanges = false;
      });
    },
    toggleSelectAll() {
      if (this.isSaving) return;
      const target = !this.isAllSelected;
      this.filteredFeatures.forEach(f => f.enabled = target);
      this.pendingChanges = true;
    }
  }
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
  overflow: hidden;
}

/* --- 2. MODULE CARD STYLING --- */
.feature-card {
  background: transparent !important;
  border: none !important;
  flex: 1;
  display: flex;
  flex-direction: column;

  :deep(.el-card__body) {
    padding: 24px;
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow-y: auto; /* Internal scroll only */
  }
}

.feature-groups-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.group-title {
  font-size: 16px;
  font-family: $font-mono;
  color: $text-muted;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  
  i { color: $accent-cyan; }
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

/* --- 3. FEATURE ITEM (TILE) STYLING --- */
.feature-card-item {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid $border-color;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.25s ease;
  display: flex;
  flex-direction: column;
  position: relative;

  &:hover {
    border-color: rgba(0, 240, 255, 0.4);
    background: rgba(0, 240, 255, 0.02);
  }

  &.feature-enabled {
    border-color: $accent-cyan;
    box-shadow: 0 0 15px rgba(0, 240, 255, 0.15);
    background: rgba(0, 240, 255, 0.05);

    .feature-name { color: $accent-cyan; }
  }

  &.is-loading {
    opacity: 0.6;
    pointer-events: none;
  }
}

.feature-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.feature-name {
  font-size: 16px;
  font-weight: bold;
  color: $text-main;
  margin: 0;
  font-family: $font-mono;
}

.feature-description {
  font-size: 13px;
  color: $text-muted;
  line-height: 1.5;
  margin: 0;
}

/* --- 4. UI ELEMENTS --- */
.btn-select-all, .reset-btn {
  background: $bg-panel-hover !important;
  border: 1px solid $border-color !important;
  color: $text-main !important;
  border-radius: 4px;
}

.save-btn {
  border-radius: 4px;
  font-weight: bold;
}

.footer-container { padding: 10px 0; }

/* Scrollbar styling for the dark theme */
:deep(.el-card__body)::-webkit-scrollbar { width: 6px; }
:deep(.el-card__body)::-webkit-scrollbar-thumb {
  background: $border-color;
  border-radius: 10px;
}
</style>