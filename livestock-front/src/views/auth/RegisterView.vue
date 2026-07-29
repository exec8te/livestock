<script lang="ts" setup>
import {ref} from "vue";
import {RouterLink, useRouter} from "vue-router";
import axios from "axios";
import {register} from "@/services/AuthService";
import type {RegisterDto} from "@/dto/auth/RegisterDto.ts";

const router = useRouter();

const formData = ref({
  email: "",
  fullName: "",
  password: "",
  confirmPassword: ""
});

type FormErrors = {
  email?: string;
  password?: string;
  confirmPassword?: string;
  fullName?: string;
  global?: string;
};

const errors = ref<FormErrors>({});
const isSubmitting = ref(false);

function validateForm(): boolean {
  errors.value = {};

  let isValid = true;

  if (!formData.value.email.trim()) {
    errors.value.email = "Email is required";
    isValid = false;
  }

  if (!formData.value.fullName.trim()) {
    errors.value.fullName = "Full Name est requis.";
    isValid = false;
  }

  if (!formData.value.password.trim()) {
    errors.value.password = "Password is required";
    isValid = false;
  } else if (formData.value.password.length < 12) {
    errors.value.password = "Password must be at least 12 characters";
    isValid = false;
  }

  if (formData.value.password !== formData.value.confirmPassword) {
    errors.value.confirmPassword = "Passwords dont match";
    isValid = false;
  }

  return isValid;
}

async function submitForm() {
  if (!validateForm()) return;

  isSubmitting.value = true;
  errors.value.global = undefined;

  try {
    const {confirmPassword, ...registerData} = formData.value;
    await register(registerData as RegisterDto);

    router.push({name: "profil"});
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      errors.value.global =
          error.response?.data?.error ??
          error.response?.data?.errors ??
          error.response?.data?.detail ??
          error.response?.data?.message ??
          "Échec de l'inscription.";
    } else {
      errors.value.global = "Une erreur inattendue est survenue.";
    }
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<template>
  <form class="register-form" @submit.prevent="submitForm">
    <h2 class="register-form__title">Sign on</h2>

    <div v-if="errors.global" class="register-form__error register-form__error--global">
      {{ errors.global }}
    </div>

    <div class="register-form__group">
      <label class="register-form__label" for="fullName">Full Name</label>
      <input
          id="fullName"
          v-model="formData.fullName"
          :class="{ 'register-form__input--error': errors.fullName }"
          class="register-form__input"
          placeholder="Jean Dupont"
          type="text"
      />
      <span v-if="errors.fullName" class="register-form__error">{{ errors.fullName }}</span>
    </div>

    <div class="register-form__group">
      <label class="register-form__label" for="email">Email</label>
      <input
          id="email"
          v-model="formData.email"
          :class="{ 'register-form__input--error': errors.email }"
          class="register-form__input"
          placeholder="email@exemple.com"
          type="email"
      />
      <span v-if="errors.email" class="register-form__error">{{ errors.email }}</span>
    </div>

    <div class="register-form__group">
      <label class="register-form__label" for="password">Password</label>
      <small class="register-form__hint">
        Password must be at least 12 characters long <br>
        and contain one uppercase
        letter, one lowercase letter, <br>
        one number, and one special character.
      </small>
      <input
          id="password"
          v-model="formData.password"
          :class="{ 'register-form__input--error': errors.password }"
          class="register-form__input"
          placeholder="************"
          type="password"
      />
      <span v-if="errors.password" class="register-form__error">{{ errors.password }}</span>
    </div>

    <div class="register-form__group">
      <label class="register-form__label" for="confirmPassword">Confirm password</label>
      <input
          id="confirmPassword"
          v-model="formData.confirmPassword"
          :class="{ 'register-form__input--error': errors.confirmPassword }"
          class="register-form__input"
          placeholder="************"
          type="password"
      />
      <span v-if="errors.confirmPassword" class="register-form__error">{{ errors.confirmPassword }}</span>
    </div>

    <button :disabled="isSubmitting" class="register-form__button" type="submit">
      {{ isSubmitting ? 'Signing on...' : "Sign on" }}
    </button>
    <div class="register-form__footer">
      <p>Already have an account ?
        <RouterLink class="register-form__link" to="/login">
          Sign in
        </RouterLink>
      </p>
    </div>
  </form>

</template>

<style scoped>
.register-form {
  width: min(80vw, 450px);
  margin: 40px auto;
  padding: 25px;
  background: var(--color-background-secondary);
  border-radius: 12px;
  box-shadow: 0 4px 20px var(--color-shadow);
}

.register-form__title {
  text-align: center;
  margin-bottom: 25px;
  color: var(--color-text-primary);
}

.register-form__hint {
  display: block;
  margin-top: 4px;
  font-size: 0.8rem;
  color: var(--color-text-tertiary);
  line-height: 1.4;
}

.register-form__group {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.register-form__label {
  font-weight: 600;
  color: var(--color-text-primary);
}

.register-form__input {
  padding: 10px 12px;
  border: 1px solid var(--color-border-primary);
  border-radius: 8px;
  outline: none;
  transition: 0.2s;
  font-family: inherit;
}

.register-form__input:focus {
  border-color: var(--color-background-tertiary);
  box-shadow: 0 0 0 2px var(--color-text-quaternary);
}

.register-form__input--error {
  border-color: var(--color-error-primary);
}

.register-form__error {
  color: var(--color-error-primary);
  font-size: 13px;
}

.register-form__error--global {
  background-color: var(--color-error-background);
  border: 1px solid var(--color-error-border);
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 18px;
}

.register-form__button {
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

.register-form__button:hover:not(:disabled) {
  background: var(--color-text-secondary);
  box-shadow: 0 0 0 2px var(--color-text-quaternary);
}

.register-form__button:disabled {
  background: #aaa;
  cursor: not-allowed;
}

.register-form__footer {
  text-align: center;
  margin-top: 1rem;
  font-size: .9rem;
}

.register-form__link {
  color: var(--color-text-primary);
  text-decoration: underline;
}

.register-form__link:hover {
  color: var(--color-text-secondary);
}
</style>