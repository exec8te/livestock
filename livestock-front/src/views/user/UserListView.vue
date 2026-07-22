<script lang="ts" setup>
import {onMounted, ref} from "vue";
import * as userService from "@/services/UserService.ts";
import type {User} from "@/types/user/User.ts";

const users = ref<User[]>([]);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    const response = await userService.getAll();
    users.value = response.data.content;

  } catch (err: unknown) {
    if (err instanceof Error) {
      error.value = "Impossible de charger les utilisateurs : " + err.message;
    } else {
      error.value = "Impossible de charger les utilisateurs.";
    }

    console.error("Erreur :", err);
  }
});
</script>

<template>
  <div>
    <h1>Liste des utilisateurs</h1>

    <div v-if="error" class="error">
      Erreur : {{ error }}
    </div>

    <table v-else>
      <thead>
      <tr>
        <th>Nom</th>
        <th>Email</th>
        <th>Role</th>
        <th>Date modification</th>
        <th>Date creation</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="user in users" :key="user.email">
        <td>{{ user.fullName }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.role }}</td>
        <td>{{ user.updatedAt }}</td>
        <td>{{ user.createdAt }}</td>
      </tr>
      </tbody>
    </table>
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