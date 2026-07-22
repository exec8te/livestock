<script setup lang="ts">
import NavBar from "@/components/NavBar.vue";
</script>

<template>
  <div id="app">
    <NavBar />

    <main class="container">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
  color: white;
}
</style>