<script lang="ts" setup>
import {onMounted, ref} from "vue";
import * as userService from "@/services/UserService.ts";
import type {UserDto} from "@/dto/user/UserDto.js";

const users = ref<UserDto[]>([]);
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

function toggleSelectAll() {
}
</script>

<template>
  <section class="main">
    <div class="main-header">
      <h1 class="main-header__title">Liste des utilisateurs</h1>
    </div>


    <div v-if="error" class="error">
      Erreur : {{ error }}
    </div>


    <div v-else class="table">
      <table class="table__table">
        <thead class="table__head">
        <tr class="table__row table__row--head">
          <th class="table__cell table__cell--head table__cell--checkbox">
            <input class="table__checkbox" type="checkbox" @click="toggleSelectAll()"/>
          </th>
          <th class="table__cell table__cell--head">Full name</th>
          <th class="table__cell table__cell--head">Email</th>
          <th class="table__cell table__cell--head">Role</th>
          <th class="table__cell table__cell--head">Date modification</th>
          <th class="table__cell table__cell--head">Date creation</th>
          <th class="table__cell table__cell--head">Action</th>
        </tr>
        </thead>

        <tbody class="table__body">
        <tr
            v-for="user in users"
            :key="user.id"
            class="table__row table__row--body"
        >
          <td class="table__cell table__cell--checkbox">
            <input class="table__checkbox" type="checkbox"/>
          </td>

          <td class="table__cell table__cell--person">
            <img
                :alt="user.fullName"
                class="table__avatar"
                src="/public/favicon.svg"
            />
            <span class="table__person-name">{{ user.fullName }}</span>
          </td>

          <td class="table__cell table__cell--email">{{ user.email }}</td>

          <td class="table__cell">
            <span
                :class="`table__badge--${user.role?.toLowerCase()}`"
                class="table__badge"
            >
              {{ user.role }}
            </span>
          </td>

          <td class="table__cell table__cell--date">{{ user.updatedAt }}</td>
          <td class="table__cell table__cell--date">{{ user.createdAt }}</td>
          <td class="table__cell table__cell--action">
            <div class="table__actions">
              <RouterLink
                  :to="{ name: 'user-edit', params: { id: user.id } }"
                  class="form-action form-action--edit"
              >
                <i class="fa-solid fa-pen-to-square"></i>
              </RouterLink>

              <button class="form-action form-action--delete" @click="">
                <i class="fa-solid fa-trash"></i>
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.main {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: start;
  justify-content: flex-start;
  background: var(--color-background-primary);
}

.main-header {
  width: 100%;
  padding: 1rem 1rem;
  border-bottom: 1px solid var(--color-border-primary);
}

.main-header__title {
  color: var(--color-text-primary);
  font-size: 1.5rem;
  font-weight: 500;
}

.table {
  overflow: hidden;
  font-family: var(--font-primary);
  width: 100%;
  padding: 1rem;
}

.table__table {
  width: 100%;
  border-collapse: collapse;
}

.table__row--body {
  transition: background-color 0.15s ease;
}

.table__row--body:hover {
  background: var(--color-background-primary);
}

.table__row--body:last-child {
  border-bottom: none;
}

.table__cell {
  padding: 16px 20px;
  text-align: left;
  font-size: 14px;
  color: var(--color-text-primary);
  white-space: nowrap;
}

.table__cell--head {
  font-weight: 500;
  font-size: 13px;
  color: var(--color-text-tertiary);
  text-transform: none;
}

.table__cell--checkbox {
  width: 40px;
  padding-right: 0;
}

.table__cell--person {
  display: flex;
  align-items: center;
  gap: 10px;
}

.table__cell--email,
.table__cell--date {
  color: var(--color-text-tertiary);
}

.table__checkbox {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  accent-color: var(--color-background-tertiary);
  cursor: pointer;
}

.table__avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.table__person-name {
  font-weight: 600;
  color: var(--color-text-primary);
}

.table__badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: var(--color-background-link-primary);
  color: var(--color-text-secondary);
}

.table__badge--admin,
.table__badge--customer {
  background: var(--color-background-link-primary);
  color: var(--color-text-secondary);
}

.table__badge--manager,
.table__badge--personal {
  background: rgb(255 178 102 / 0.24);
  color: #c9740a;
}

.table__badge--user,
.table__badge--employees {
  background: rgb(66 184 131 / 0.16);
  color: var(--color-success-hover);
}

.table__actions {
  display: flex;
  align-items: center;
  gap: .5rem;
}

.form-action {
  width: 2.25rem;
  height: 2.25rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  border: none;
  border-radius: .25rem;
  background: transparent;

  color: var(--color-text-tertiary);
  text-decoration: none;
  cursor: pointer;

  transition: background-color .2s ease,
  color .2s ease,
  transform .15s ease;
}

.form-action i {
  font-size: 1.15rem;
  line-height: 1;
}

.form-action--edit:hover {
  background: var(--color-background-link-primary);
  color: var(--color-text-secondary);
}

.form-action--delete:hover {
  background: var(--color-error-background);
  color: var(--color-error-primary);
}

.form-action:active {
  transform: scale(.95);
}
</style>