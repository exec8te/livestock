<script lang="ts" setup>
import {storeToRefs} from "pinia";
import {useAuthStore} from "@/stores/auth";

const authStore = useAuthStore();

const {user} = storeToRefs(authStore);
</script>

<template>
  <div v-if="user">
    <h1>{{ user.fullName }}</h1>
    <p>{{ user.email }}</p>
    <p>{{ user.role }}</p>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
  color: #333;
}

table {
  width: 100%;
  border-collapse: collapse;
}

.error {
  color: red;
  margin-bottom: 1rem;
}
</style>