<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import {getUserById} from "@/services/UserService";
import type {UserDto} from "@/dto/user/UserDto";

const route = useRoute();

const id = route.params.id as string;

const user = ref<UserDto | null>(null);

onMounted(async () => {
  try {
    const response = await getUserById(id);
    user.value = response.data;
  } catch (error) {
    console.error(error);
  }
});
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