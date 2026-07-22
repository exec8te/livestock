<script setup>
import { RouterLink } from "vue-router";
</script>

<template>
  <nav class="navbar">
    <RouterLink to="/" class="nav-link">Accueil</RouterLink>
    <RouterLink to="/users" class="nav-link">Utilisateurs</RouterLink>
  </nav>
</template>

<style scoped>
.navbar {
  display: flex;
  gap: 20px;
  padding: 10px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-weight: 500;
  padding: 5px 10px;
  border-radius: 4px;
}

.nav-link:hover {
  background-color: #f0f0f0;
}

.nav-link.router-link-exact-active {
  color: #42b983;
  background-color: #0f75da;
}
</style>
