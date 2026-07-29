<script lang="ts" setup>
import {ref} from "vue";
import {RouterLink, useRouter} from "vue-router";
import axios from "axios";
import type {LoginDto} from "@/dto/auth/LoginDto";
import {useAuthStore} from "@/stores/auth";

const authStore = useAuthStore();
const router = useRouter();

const formData = ref<LoginDto>({
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
  <form class="login-form" @submit.prevent="submitForm">
    <h2 class="login-form__title">Sign in</h2>

    <div v-if="errors.global" class="login-form__error login-form__error--global">
      {{ errors.global }}
    </div>

    <div class="login-form__group">
      <label class="login-form__label" for="email">Email</label>
      <input
          id="email"
          v-model="formData.email"
          :class="{ 'login-form__input--error': errors.email }"
          class="login-form__input"
          placeholder="email@example.com"
          type="email"
      />
      <span v-if="errors.email" class="login-form__error">{{ errors.email }}</span>
    </div>

    <div class="login-form__group">
      <label class="login-form__label" for="password">Password</label>
      <input
          id="password"
          v-model="formData.password"
          :class="{ 'login-form__input--error': errors.password }"
          class="login-form__input"
          placeholder="********"
          type="password"
      />
      <span v-if="errors.password" class="login-form__error">{{ errors.password }}</span>
      <RouterLink class="login-form__link" to="/register">
        Forgot password ?
      </RouterLink>
    </div>

    <button :disabled="isSubmitting" class="login-form__button" type="submit">
      {{ isSubmitting ? 'Signing in...' : 'Sign in' }}
    </button>
    <div class="login-form__footer">
      <p>Don't have any account ?
        <RouterLink class="login-form__link" to="/register">
          Sign on
        </RouterLink>
      </p>
    </div>
  </form>

</template>

<style scoped>
.login-form {
  width: min(80vw, 450px);
  margin: 40px auto;
  padding: 25px;
  background: var(--color-background-secondary);
  border-radius: 12px;
  box-shadow: 0 4px 20px var(--color-shadow);
}

.login-form__title {
  text-align: center;
  margin-bottom: 25px;
  color: var(--color-text-primary);
}

.login-form__group {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.login-form__label {
  font-weight: 600;
  color: var(--color-text-primary);
}

.login-form__input {
  padding: 10px 12px;
  border: 1px solid var(--color-border-primary);
  border-radius: 8px;
  outline: none;
  transition: 0.2s;
}

.login-form__input:focus {
  border-color: var(--color-background-tertiary);
  box-shadow: 0 0 0 2px var(--color-text-quaternary);
}

.login-form__input--error {
  border-color: var(--color-error-primary);
}

.login-form__error {
  color: var(--color-error-primary);
  font-size: 13px;
}

.login-form__error--global {
  background-color: var(--color-error-background);
  border: 1px solid var(--color-error-border);
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 18px;
}

.login-form__button {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  background: var(--color-background-tertiary);
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}

.login-form__button:hover:not(:disabled) {
  background: var(--color-text-secondary);
  border-color: var(--color-background-tertiary);
  box-shadow: 0 0 0 2px var(--color-text-quaternary);
}

.login-form__button:disabled {
  background: #aaa;
  cursor: not-allowed;
}

.login-form__footer {
  text-align: center;
  margin-top: .2rem;
  font-size: .8rem;
}

.login-form__link {
  color: var(--color-text-primary);
  text-decoration: underline;
  font-size: .8rem;

}

.login-form__link:hover {
  color: var(--color-text-secondary);
}
</style>