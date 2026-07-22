<script lang="ts" setup>
import {ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";
import type {Login} from "@/types/auth/Login";
import {useAuthStore} from "@/stores/auth";

const authStore = useAuthStore();
const router = useRouter();

const formData = ref<Login>({
  email: "",
  password: ""
});

type FormErrors = {
  email?: string;
  password?: string;
  global?: string;
};

const errors = ref<FormErrors>({});
const isSubmitting = ref(false);

function validateForm(): boolean {
  errors.value = {};

  let isValid = true;

  if (!formData.value.email.trim()) {
    errors.value.email = "L'email est obligatoire.";
    isValid = false;
  }

  if (!formData.value.password.trim()) {
    errors.value.password = "Le mot de passe est obligatoire.";
    isValid = false;
  }

  return isValid;
}

async function submitForm() {
  if (!validateForm()) return;

  isSubmitting.value = true;
  errors.value.global = undefined;

  try {
    await authStore.login(
        formData.value
    );

    router.push({name: "profil"});
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      errors.value.global =
          error.response?.data?.error ??
          error.response?.data?.detail ??
          error.response?.data?.message;
    } else {
      errors.value.global = "Une erreur inattendue est survenue. ";
    }
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<template>
  <form @submit.prevent="submitForm">
    <div>
      <label for="email">Email</label>
      <input
          id="email"
          v-model="formData.email"
          autocomplete="email"
          type="email"
      />
      <span v-if="errors.email" class="error">
        {{ errors.email }}
      </span>
    </div>

    <div>
      <label for="password">Mot de passe</label>
      <input
          id="password"
          v-model="formData.password"
          autocomplete="current-password"
          type="password"
      />
      <span v-if="errors.password" class="error">
        {{ errors.password }}
      </span>
    </div>

    <button :disabled="isSubmitting" type="submit">
      {{ isSubmitting ? "Connexion..." : "Se connecter" }}
    </button>

    <p v-if="errors.global" class="error">
      {{ errors.global }}
    </p>
  </form>
</template>

<style scoped>
form {
  max-width: 500px;
  margin: 40px auto;
  padding: 25px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  font-family: Arial, sans-serif;
}

div {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

label {
  font-weight: 600;
  color: #333;
}

input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
  transition: 0.2s;
}

input:focus {
  border-color: #42b883;
  box-shadow: 0 0 0 2px rgba(66, 184, 131, 0.2);
}

button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  background: #42b883;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}

button:hover:not(:disabled) {
  background: #369f6e;
}

button:disabled {
  background: #aaa;
  cursor: not-allowed;
}

.error {
  color: #e74c3c;
  font-size: 13px;
}
</style>