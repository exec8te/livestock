<script lang="ts" setup>
import Header from "@/components/Header.vue";
</script>

<template>
  <div id="app-layout">
    <Header/>
    <main class="main-content">
      <RouterView/>
    </main>
  </div>
</template>

<style scoped>
#app-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}
</style>