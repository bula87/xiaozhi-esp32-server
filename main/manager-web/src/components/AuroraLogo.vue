<template>
  <svg
    class="aurora-logo"
    viewBox="0 0 40 40"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <rect
      x="2"
      y="2"
      width="36"
      height="36"
      rx="8"
      fill="var(--ui-logo-bg)"
      stroke="var(--ui-logo-frame)"
      stroke-opacity="0.85"
      stroke-width="1.25"
    />
    <path
      d="M12 26 L20 12 L28 26 M15 22 H25"
      fill="none"
      stroke="var(--ui-logo-a)"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    />
    <circle cx="20" cy="28" r="2" fill="var(--ui-logo-dot)" />
  </svg>
</template>

<script>
export default {
  name: "AuroraLogo",
};
</script>

<style scoped>
.aurora-logo {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  display: block;
}
</style>
