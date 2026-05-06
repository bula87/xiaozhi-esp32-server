<template>
  <el-dialog
    :title="$t('chatHistory.with') + agentName"
    :visible.sync="dialogVisible"
    width="80%"
    custom-class="aurora-dialog chat-history-dialog"
    :append-to-body="true"
  >
    <div class="chat-container">
      <div class="session-sidebar">
         <div v-for="session in sessions" :key="session.sessionId" 
              class="session-item" :class="{ active: currentSessionId === session.sessionId }"
              @click="selectSession(session)">
            <img :src="getUserAvatar(session.sessionId)" class="avatar" />
            <div class="session-info">
               <span class="time">{{ formatTime(session.createdAt) }}</span>
            </div>
         </div>
      </div>
      <div class="chat-main aurora-scroll">
         <div v-for="msg in messagesWithTime" :key="msg.id" class="msg-wrapper">
            </div>
      </div>
    </div>
  </el-dialog>
</template>

<style lang="scss">
@import "../styles/aurora-theme.scss";

.chat-history-dialog {
  background: $bg-panel !important;
  .el-dialog__header { border-bottom: 1px solid $border-color; }
  .el-dialog__title { color: $accent-cyan !important; font-family: $font-mono; }
}

.chat-container {
  display: flex;
  height: 70vh;
  background: $bg-base;
}

.session-sidebar {
  width: 200px;
  border-right: 1px solid $border-color;
  background: rgba(0,0,0,0.2);
  .session-item {
    padding: 12px;
    border-bottom: 1px solid rgba(255,255,255,0.05);
    cursor: pointer;
    &.active { background: rgba(0, 240, 255, 0.05); border-left: 3px solid $accent-cyan; }
  }
}

.chat-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
/* User message bubbles */
.user-message .message-content {
  background: $accent-cyan !important;
  color: black !important;
}
/* Bot message bubbles */
.bot-message .message-content {
  background: $bg-panel-hover !important;
  color: $text-main !important;
  border: 1px solid $border-color;
}
</style>