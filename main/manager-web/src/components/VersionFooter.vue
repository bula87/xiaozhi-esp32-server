<template>
  <div class="copyright-container">
    <div class="footer-content">
      <span class="version-text">{{ year }} {{ name }} {{ version }}</span>
      <template v-if="beianGaNum !== 'null'">
        <span class="divider">|</span>
        <a :href="'http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=' + beianGaNum" target="_blank" class="beian-link">
          <span class="beian-text">{{ beianGaNum }}</span>
        </a>
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import "../styles/aurora-theme.scss";

.copyright-container {
  padding: 15px 0;
  text-align: center;
}

.footer-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 12px;
  font-family: $font-mono;
  color: $text-muted;
}

.version-text { opacity: 0.8; }
.divider { color: $border-color; }

.beian-link {
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  &:hover .beian-text { color: $accent-cyan; }
}

.beian-text {
  color: $text-muted;
  transition: color 0.2s;
}
</style>