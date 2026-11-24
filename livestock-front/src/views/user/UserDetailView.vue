<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";
import * as userService from "@/services/UserService.js";
import type {UserUpdateDto} from "@/dto/user/UserUpdateDto.ts";
import {useAuthStore} from "@/stores/auth.ts";

const router = useRouter();

const authStore = useAuthStore();

const formData = ref({
  email: "",
  fullName: "",
  disabled: false,
  role: "",
  createdAt: null as Date | null,
  updatedAt: null as Date | null
});

type FormErrors = {
  email?: string;
  fullName?: string;
  role?: string;
  disabled?: string;
  global?: string;
};

const errors = ref<FormErrors>({});
const isSubmitting = ref(false);
const isLoadingUser = ref(false);

const pageTitle = "Edit user";

async function fetchUser() {
  try {
    const response = await userService.getMe();
    const user = response.data;

    formData.value.email = user.email;
    formData.value.fullName = user.fullName;
    formData.value.disabled = user.disabled;
    formData.value.createdAt = new Date(user.createdAt);
    formData.value.updatedAt = user.updatedAt ? new Date(user.updatedAt) : null;
    formData.value.role = user.role;

  } catch (err: unknown) {
    if (axios.isAxiosError(err)) {
      errors.value.global = err.response?.data?.message ?? "Impossible de charger l'utilisateur.";
    } else {
      errors.value.global = "Impossible de charger l'utilisateur.";
    }
    console.error("Erreur :", err);
  }
}

onMounted(async () => {
  isLoadingUser.value = true;
  try {
    await Promise.all([fetchUser()]);
  } finally {
    isLoadingUser.value = false;
  }
});

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

  return isValid;
}

async function submitForm() {
  if (!validateForm()) return;

  isSubmitting.value = true;
  errors.value.global = undefined;

  try {
    const payload: UserUpdateDto = {
      email: formData.value.email,
      fullName: formData.value.fullName,
    };

    const response = await userService.updateMe(payload);
    authStore.setUser(response.data);

    router.push({name: "profil"});

  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      errors.value.global =
          error.response?.data?.error ??
          error.response?.data?.errors ??
          error.response?.data?.detail ??
          error.response?.data?.message ??
          "Échec de la modification de l'utilisateur.";
    } else {
      errors.value.global = "Une erreur inattendue est survenue.";
    }
  } finally {
    isSubmitting.value = false;
  }
}

function cancel() {
  router.push({name: "profil"});
}
</script>

<template>
  <section class="main">
    <div class="main-header">
      <h1 class="main-header__title">{{ pageTitle }}</h1>
      <div class="header__actions">
      </div>
    </div>

    <div v-if="errors.global" class="alert alert--error">
      {{ errors.global }}
    </div>

    <div v-if="isLoadingUser" class="edit-form edit-form--loading">
      Loading user...
    </div>

    <form v-else id="edit-form" class="edit-form" @submit.prevent="submitForm">
      <section class="edit-form__section">
        <h3 class="edit-form__section-title">General informations</h3>

        <div class="edit-form__section-informations">
          <p v-if="formData.createdAt" class="information__date">
            <i class="fa-regular fa-calendar"></i>
            Created <span class="information__date-value">{{ formData.createdAt.toLocaleString() }}</span>
          </p>
          <p v-if="formData.updatedAt" class="information__date">
            <i class="fa-regular fa-clock"></i>
            Last updated <span class="information__date-value">{{ formData.updatedAt.toLocaleString() ?? "--" }}</span>
          </p>
        </div>

        <div class="edit-form__grid">
          <div class="edit-form__group">
            <label class="edit-form__label" for="fullName">Full name</label>
            <input
                id="fullName"
                v-model="formData.fullName"
                :class="{ 'edit-form__input--error': errors.fullName }"
                class="edit-form__input"
                placeholder="Jean Dupont"
                type="text"
            />
            <span v-if="errors.fullName" class="edit-form__error">{{ errors.fullName }}</span>
          </div>

          <div class="edit-form__group">
            <label class="edit-form__label" for="email">Email</label>
            <input
                id="email"
                v-model="formData.email"
                :class="{ 'edit-form__input--error': errors.email }"
                class="edit-form__input"
                placeholder="email@example.com"
                type="email"
            />
            <span v-if="errors.email" class="edit-form__error">{{ errors.email }}</span>
          </div>

          <div class="edit-form__group">
            <label class="edit-form__label" for="role">Role</label>
            <input
                id="role"
                v-model="formData.role"
                :class="{ 'edit-form__input--error': errors.role }"
                class="edit-form__input"
                disabled
            />
            <span v-if="errors.role" class="edit-form__error">{{ errors.role }}</span>
          </div>
        </div>
      </section>

      <div class="edit-form__footer">
        <button class="btn btn--ghost" type="button" @click="cancel">Cancel</button>
        <button :disabled="isSubmitting" class="btn btn--primary" type="submit">
          {{ isSubmitting ? 'Saving...' : "Save" }}
        </button>
      </div>
    </form>
  </section>
</template>

<style scoped>

.alert--error {
  background-color: var(--color-error-background);
  border: 1px solid var(--color-error-border);
  color: var(--color-error-primary);
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 0.9rem;
}

.edit-form {
  width: 100%;
  background: var(--color-background-primary);
  padding: 1rem 5rem 1rem 2rem;
}

.edit-form--loading {
  color: var(--color-text-tertiary);
  padding: 40px 0;
}

.edit-form__section {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.edit-form__section-informations {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.information__date {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  border-radius: 20px;
  background: var(--color-background-input-disabled);
  color: var(--color-text-tertiary);
  font-size: 0.78rem;
  font-weight: 500;
}

.information__date i {
  font-size: 0.75rem;
  opacity: 0.8;
}

.information__date-value {
  color: var(--color-text-primary);
  font-weight: 600;
}

.edit-form__section-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.edit-form__toggle input {
  accent-color: var(--color-background-tertiary);
  cursor: pointer;
}

.edit-form__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px 28px;
}

.edit-form__group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.edit-form__group--checkbox {
  justify-content: center;
}

.edit-form__checkbox-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.edit-form__checkbox {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  accent-color: var(--color-background-tertiary);
  cursor: pointer;
}

.edit-form__checkbox-label {
  color: var(--color-text-primary);
  cursor: pointer;
  font-weight: 500;
}

.edit-form__label {
  font-weight: 600;
  font-size: 0.9rem;
  color: var(--color-text-primary);
}

.edit-form__hint {
  display: block;
  font-size: 0.8rem;
  color: var(--color-text-tertiary);
  line-height: 1.4;
}

.edit-form__hint--muted {
  font-style: italic;
}

.edit-form__input {
  padding: 10px 12px;
  border: 1px solid var(--color-border-primary);
  border-radius: 8px;
  outline: none;
  transition: 0.2s;
  font-family: inherit;
  font-size: 0.95rem;
  background: var(--color-background-primary);
  color: var(--color-text-primary);
}

.edit-form__input:disabled {
  background: var(--color-background-input-disabled);
}

.edit-form__input:focus {
  border-color: var(--color-background-tertiary);
  box-shadow: 0 0 0 3px var(--color-text-quaternary);
}

.edit-form__input--error {
  border-color: var(--color-error-primary);
}

.edit-form__error {
  color: var(--color-error-primary);
  font-size: 13px;
}

.edit-form__footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--color-background-input-disabled);
}

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: 0.2s;
  border: 1px solid transparent;
  font-family: inherit;
}

.btn--primary {
  background: var(--color-gradiant-primary);
  color: white;
  box-shadow: 0 2px 10px var(--color-shadow);
}

.btn--primary:hover:not(:disabled) {
  box-shadow: 0 0 0 3px var(--color-text-quaternary);
}

.btn--primary:disabled {
  background: #aaa;
  cursor: not-allowed;
  box-shadow: none;
}

.btn--ghost {
  background: transparent;
  border-color: var(--color-border-primary);
  color: var(--color-text-primary);
}

.btn--ghost:hover {
  background: var(--color-background-link-primary);
}

@media (max-width: 900px) {
  .edit-form__grid {
    grid-template-columns: 1fr;
  }
}
</style>