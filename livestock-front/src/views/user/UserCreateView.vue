<script lang="ts" setup>
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";
import {createUser} from "@/services/UserService.ts";
import type {RoleDto} from "@/dto/role/RoleDto.ts";
import * as roleService from "@/services/RoleService.ts";
import type {UserCreateDto} from "@/dto/user/UserCreateDto.ts";

const router = useRouter();

const formData = ref({
  email: "",
  fullName: "",
  disabled: false,
  password: "",
  confirmPassword: "",
  roleId: null as number | null
});

type FormErrors = {
  email?: string;
  password?: string;
  confirmPassword?: string;
  fullName?: string;
  disabled?: string;
  roleId?: string;
  global?: string;
};

const errors = ref<FormErrors>({});
const isSubmitting = ref(false);
const roles = ref<RoleDto[]>([]);
const isLoadingRoles = ref(false);
const search = ref("");

async function fetchRoles() {
  isLoadingRoles.value = true;
  try {
    const response = await roleService.getAllForUsers(
        {search: search.value || undefined},
    );

    roles.value = response.data.content;
    errors.value.global = undefined;
  } catch (err: unknown) {
    if (err instanceof Error) {
      errors.value.global = "Impossible de charger les rôles : " + err.message;
    } else {
      errors.value.global = "Impossible de charger les rôles.";
    }
    console.error("Erreur :", err);
  } finally {
    isLoadingRoles.value = false;
  }
}

onMounted(() => {
  fetchRoles();
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

  if (!formData.value.roleId) {
    errors.value.roleId = "Le rôle est requis.";
    isValid = false;
  }

  return isValid;
}

async function submitForm() {
  if (!validateForm()) return;

  isSubmitting.value = true;
  errors.value.global = undefined;

  try {
    const {confirmPassword, ...createUserData} = formData.value;
    await createUser(createUserData as UserCreateDto);

    router.push({name: "users"});
  } catch (error: unknown) {
    if (axios.isAxiosError(error)) {
      errors.value.global =
          error.response?.data?.error ??
          error.response?.data?.errors ??
          error.response?.data?.detail ??
          error.response?.data?.message ??
          "Échec de la création de l'utilisateur.";
    } else {
      errors.value.global = "Une erreur inattendue est survenue.";
    }
  } finally {
    isSubmitting.value = false;
  }
}

function cancel() {
  router.push({name: "users"});
}
</script>

<template>
  <section class="main">
    <div class="main-header">
      <h1 class="main-header__title">Create a user</h1>
      <div class="header__actions">
      </div>
    </div>

    <div v-if="errors.global" class="alert alert--error">
      {{ errors.global }}
    </div>

    <form id="register-form" class="register-form" @submit.prevent="submitForm">
      <section class="register-form__section">
        <h3 class="register-form__section-title">General informations</h3>
        <div class="register-form__grid">
          <div class="register-form__group">
            <label class="register-form__label" for="fullName">Full name</label>
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
                placeholder="email@example.com"
                type="email"
            />
            <span v-if="errors.email" class="register-form__error">{{ errors.email }}</span>
          </div>

          <div class="register-form__group">
            <label class="register-form__label" for="role">Role</label>
            <select
                id="role"
                v-model="formData.roleId"
                :class="{ 'register-form__input--error': errors.roleId }"
                :disabled="isLoadingRoles"
                class="register-form__input"
            >
              <option :value="null" disabled>
                {{ isLoadingRoles ? 'Roles loading...' : 'Select a role' }}
              </option>
              <option v-for="role in roles" :key="role.id" :value="role.id">
                {{ role.name }}
              </option>
            </select>
            <span v-if="errors.roleId" class="register-form__error">{{ errors.roleId }}</span>
          </div>

          <div class="register-form__group register-form__group--checkbox">
            <label class="register-form__label" for="disabled">Account status</label>
            <div class="register-form__checkbox-row">
              <input
                  id="disabled"
                  v-model="formData.disabled"
                  :class="{ 'register-form__input--error': errors.disabled }"
                  class="register-form__checkbox"
                  type="checkbox"
              />
              <label class="register-form__checkbox-label" for="disabled">Disabled account</label>
            </div>
            <small class="register-form__hint">
              A disabled account dan not permit to the user to sign in himself without an action of an administrator
            </small>
            <span v-if="errors.disabled" class="register-form__error">{{ errors.disabled }}</span>
          </div>
        </div>
      </section>

      <section class="register-form__section">
        <h3 class="register-form__section-title">Security</h3>
        <div class="register-form__grid">
          <div class="register-form__group">
            <label class="register-form__label" for="password">Password</label>
            <input
                id="password"
                v-model="formData.password"
                :class="{ 'register-form__input--error': errors.password }"
                class="register-form__input"
                placeholder="************"
                type="password"
            />
            <small class="register-form__hint">
              Must be at least 12 characters long and contain one uppercase letter, one lowercase letter, one number,
              and one special character.
            </small>
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
        </div>
      </section>

      <div class="register-form__footer">
        <button class="btn btn--ghost" type="button" @click="cancel">Cancel</button>
        <button :disabled="isSubmitting" class="btn btn--primary" type="submit">
          {{ isSubmitting ? 'Creation...' : "Create" }}
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

.register-form {
  width: 100%;
  background: var(--color-background-primary);
  padding: 1rem 5rem 1rem 2rem;
}

.register-form__section + .register-form__section {
  margin-top: 32px;
  padding-top: 28px;
  border-top: 1px solid var(--color-background-input-disabled);
}

.register-form__section-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 18px;
}

.register-form__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px 28px;
}

.register-form__group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.register-form__group--checkbox {
  justify-content: center;
}

.register-form__checkbox-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.register-form__checkbox {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  accent-color: var(--color-background-tertiary);
  cursor: pointer;
}

.register-form__checkbox-label {
  color: var(--color-text-primary);
  cursor: pointer;
  font-weight: 500;
}

.register-form__label {
  font-weight: 600;
  font-size: 0.9rem;
  color: var(--color-text-primary);
}

.register-form__hint {
  display: block;
  font-size: 0.8rem;
  color: var(--color-text-tertiary);
  line-height: 1.4;
}

.register-form__input {
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

.register-form__input:focus {
  border-color: var(--color-background-tertiary);
  box-shadow: 0 0 0 3px var(--color-text-quaternary);
}

.register-form__input--error {
  border-color: var(--color-error-primary);
}

.register-form__error {
  color: var(--color-error-primary);
  font-size: 13px;
}

.register-form__footer {
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
  .register-page {
    padding: 20px;
  }

  .register-form__grid {
    grid-template-columns: 1fr;
  }

  .register-page__header {
    flex-direction: column;
  }
}
</style>