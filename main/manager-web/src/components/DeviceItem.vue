<template>
  <div class="device-item aurora-card">
    <div class="header-row">
      <el-tooltip :content="device.agentName" placement="top" effect="dark">
        <div class="device-item-title">{{ device.agentName }}</div>
      </el-tooltip>
      <div class="icon-actions">
        <img src="@/assets/home/delete.png" class="action-icon delete-icon" @click.stop="handleDelete" />
        <el-tooltip effect="dark" :content="device.systemPrompt" placement="top" popper-class="aurora-tooltip"> 
          <img src="@/assets/home/info.png" class="action-icon info-icon" />
        </el-tooltip>
      </div>
    </div>

    <div class="device-details">
      <div class="detail-line">
        <span class="label">{{ $t('home.languageModel') }}:</span> {{ device.llmModelName }}
      </div>
      <div class="detail-line">
        <span class="label">{{ $t('home.voiceModel') }}:</span> {{ device.ttsModelName }} ({{ device.ttsVoiceName }})
      </div>
    </div>

    <div class="btn-grid">
      <div class="settings-btn" @click="handleConfigure">{{ $t('home.configureRole') }}</div>
      <div v-if="featureStatus.voiceprintRecognition" class="settings-btn" @click="handleVoicePrint">
        {{ $t('home.voiceprintRecognition') }}
      </div>
      <div class="settings-btn" @click="handleDeviceManage">
        {{ $t('home.deviceManagement') }}({{ device.deviceCount }})
      </div>
      <div :class="['settings-btn', { 'disabled-btn': device.memModelId === 'Memory_nomem' }]" @click="handleChatHistory">
        <el-tooltip effect="dark" v-if="device.memModelId === 'Memory_nomem'" :content="$t('home.enableMemory')" placement="top">
          <span>{{ $t('home.chatHistory') }}</span>
        </el-tooltip>
        <span v-else>{{ $t('home.chatHistory') }}</span>
      </div>
    </div>

    <div class="version-info">
      <div class="time">{{ $t('home.lastConversation') }}: {{ formattedLastConnectedTime }}</div>
      <el-tooltip :content="tags.join()" placement="top" effect="dark">
        <div class="tags-scroll">{{ tags.join() }}</div>
      </el-tooltip>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

.device-item {
  background: $bg-panel;
  border: 1px solid $border-color;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  &:hover {
    border-color: $accent-cyan;
    box-shadow: 0 0 15px rgba(0, 240, 255, 0.1);
  }
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.device-item-title {
  font-weight: bold;
  font-size: 16px;
  color: $accent-cyan;
  font-family: $font-mono;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-icon {
  width: 16px;
  height: 16px;
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.2s;
  &:hover { opacity: 1; }
}

.device-details {
  margin-bottom: 15px;
  .detail-line {
    font-size: 12px;
    color: $text-main;
    margin-bottom: 4px;
    .label { color: $text-muted; }
  }
}

.btn-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.settings-btn {
  font-size: 11px;
  color: $accent-purple;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  padding: 2px 10px;
  border-radius: 4px;
  cursor: pointer;
  &:hover { background: rgba(139, 92, 246, 0.2); color: white; }
}

.disabled-btn {
  background: rgba(255, 255, 255, 0.05) !important;
  color: $text-muted !important;
  border-color: transparent !important;
  cursor: not-allowed;
}

.version-info {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: $text-muted;
  .tags-scroll { max-width: 100px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: $accent-cyan; }
}
</style>