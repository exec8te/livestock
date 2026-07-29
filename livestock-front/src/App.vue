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
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  overflow-y: auto;
}
</style>