<script lang="ts" setup>
import {computed, onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import axios from "axios";
import type {UserUpdateAdminDto} from "@/dto/user/UserUpdateAdminDto.ts";
import * as userService from "@/services/UserService.ts";
import type {RoleDto} from "@/dto/role/RoleDto.ts";
import * as roleService from "@/services/RoleService.ts";

const route = useRoute();
const router = useRouter();

const userId = route.params.id as string;

const formData = ref({
  email: "",
  fullName: "",
  disabled: false,
  roleId: null as number | null,
});

type FormErrors = {
  email?: string;
  fullName?: string;
  roleId?: string;
  disabled?: string;
  global?: string;
};

const errors = ref<FormErrors>({});
const isSubmitting = ref(false);
const isLoadingUser = ref(false);
const isLoadingRoles = ref(false);
const roles = ref<RoleDto[]>([]);
const search = ref("");

// L'API ne renvoie que l'intitulé (name) du rôle sur l'utilisateur, pas son id.
// On le stocke ici en attendant de pouvoir le croiser avec la liste des rôles.
const currentRoleName = ref<string | null>(null);

const pageTitle = computed(() => formData.value.fullName ? `Edit ${formData.value.fullName}` : "Edit user");

async function fetchRoles() {
  isLoadingRoles.value = true;
  try {
    const response = await roleService.getAllForUsers(
        {search: search.value || undefined},
    );
    roles.value = response.data.content;
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

async function fetchUser() {
  try {
    const response = await userService.getUserById(userId);
    const user = response.data;

    formData.value.email = user.email;
    formData.value.fullName = user.fullName;
    formData.value.disabled = user.disabled;
    // `user.role` est l'intitulé du rôle (ex: "ADMIN"), pas un id.
    currentRoleName.value = user.role ?? null;
  } catch (err: unknown) {
    if (axios.isAxiosError(err)) {
      errors.value.global = err.response?.data?.message ?? "Impossible de charger l'utilisateur.";
    } else {
      errors.value.global = "Impossible de charger l'utilisateur.";
    }
    console.error("Erreur :", err);
  }
}

// Une fois la liste des rôles et le rôle de l'utilisateur chargés, on retrouve
// l'id du rôle correspondant à son nom pour pré-remplir le select.
function resolveRoleId() {
  if (!currentRoleName.value) return;

  const matchedRole = roles.value.find((r) => r.name === currentRoleName.value);
  formData.value.roleId = matchedRole ? Number(matchedRole.id) : null;
}

onMounted(async () => {
  isLoadingUser.value = true;
  try {
    await Promise.all([fetchRoles(), fetchUser()]);
    resolveRoleId();
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
    const payload: UserUpdateAdminDto = {
      email: formData.value.email,
      fullName: formData.value.fullName,
      disabled: formData.value.disabled,
      roleId: formData.value.roleId as number
    };

    await userService.updateUserAdmin(userId, payload);

    router.push({name: "users"});
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
  router.push({name: "users"});
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
            <select
                id="role"
                v-model="formData.roleId"
                :class="{ 'edit-form__input--error': errors.roleId }"
                :disabled="isLoadingRoles"
                class="edit-form__input"
            >
              <option :value="null" disabled>
                {{ isLoadingRoles ? 'Roles loading...' : 'Select a role' }}
              </option>
              <option v-for="role in roles" :key="role.id" :value="Number(role.id)">
                {{ role.name }}
              </option>
            </select>
            <span v-if="errors.roleId" class="edit-form__error">{{ errors.roleId }}</span>
          </div>

          <div class="edit-form__group edit-form__group--checkbox">
            <label class="edit-form__label" for="disabled">Account status</label>
            <div class="edit-form__checkbox-row">
              <input
                  id="disabled"
                  v-model="formData.disabled"
                  class="edit-form__checkbox"
                  type="checkbox"
              />
              <label class="edit-form__checkbox-label" for="disabled">Disabled account</label>
            </div>
            <small class="edit-form__hint">
              A disabled account dan not permit to the user to sign in himself without an action of an administrator
            </small>
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

.edit-form__section + .edit-form__section {
  margin-top: 32px;
  padding-top: 28px;
  border-top: 1px solid var(--color-background-input-disabled);
}

.edit-form__section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.edit-form__section-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--color-text-primary);
}

.edit-form__toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  color: var(--color-text-secondary);
  font-weight: 600;
  cursor: pointer;
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