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
      <div class="header__actions">
        <button class="header-action">
          <i class="fa-solid fa-arrow-up-from-bracket"></i>
        </button>
        <button class="header-action">
          <i class="fa-solid fa-ellipsis"></i>
        </button>
      </div>
    </div>


    <div v-if="error" class="error">
      Erreur : {{ error }}
    </div>


    <div v-else class="table">

      <section class="table__tools">
        <div class="tools-main">
          <button class="tool"><i class="fa-solid fa-sliders"></i><span>Filter</span></button>
          <div class="search">
            <div class="search__icon">
              <i class="fa-solid fa-magnifying-glass"></i>
            </div>

            <input
                class="search__input"
                placeholder="Rechercher un utilisateur..."
                type="text"
            />
          </div>

        </div>

        <button class="tool"><span>Clear</span></button>

      </section>

      <table class="table__table">
        <thead class="table__head">
        <tr class="table__row table__row--head">
          <th class="table__cell table__cell--head table__cell--checkbox">
            <input class="table__checkbox" type="checkbox" @click="toggleSelectAll()"/>
          </th>
          <th class="table__cell table__cell--head">
            Full name
          </th>
          <th class="table__cell table__cell--head">
            Email
          </th>
          <th class="table__cell table__cell--head">
            Role
          </th>
          <th class="table__cell table__cell--head">
            Updated at
          </th>
          <th class="table__cell table__cell--head">
            Created at
          </th>
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
  padding: 1rem 5rem 1rem 1rem;
  border-bottom: 1px solid var(--color-border-primary);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 5rem 1rem 2rem;
}

.main-header__title {
  color: var(--color-text-primary);
  font-size: 1.5rem;
  font-weight: 500;
}

.header__actions {
  display: flex;
  gap: .5rem;
  align-items: center;
}

.header-action {
  font-size: 1rem;
  font-weight: 500;
  padding: .4rem .6rem;
  background: var(--color-background-primary);
  border-radius: .4rem;
  color: var(--color-text-primary);
  border: 1px solid var(--color-text-tertiary);
  display: flex;
  align-items: center;
  transition: .15s;
}

.header-action i {
  font-size: 1.1rem;
}

.header-action:hover {
  background: var(--color-text-tertiary);
  color: var(--color-background-primary);
  cursor: pointer;
}

.table {
  overflow: hidden;
  width: 100%;
  padding: 2rem 5rem 2rem 2rem;
}

.table__tools {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tools-main {
  width: 100%;
  display: flex;
  align-items: center;
  gap: .8rem;
}


.tool {
  padding: .3rem .7rem;
  background: var(--color-background-primary);
  border-radius: .8rem;
  color: var(--color-text-primary);
  border: 1px solid var(--color-border-primary);
  display: flex;
  gap: .4rem;
  align-items: center;
  font-size: .8rem;
  height: 1.8rem;
  cursor: pointer;
  transition: .15s;
}

.tool i {
  font-size: .8rem;
}

.tool:hover {
  background: var(--color-text-primary);
  border-color: var(--color-text-primary);
  color: var(--color-background-primary);
}

.search {
  display: flex;
  align-items: center;

  width: 250px;
  height: 1.8rem;

  border: 1px solid var(--color-border-primary);
  border-radius: .75rem;
  overflow: hidden;

  background: var(--color-background-primary);
  transition: .2s;

  font-size: .9rem;

}

.search__icon {
  width: 2.8rem;
  height: 100%;

  display: flex;
  justify-content: center;
  align-items: center;

  background: var(--color-text-primary);
  color: var(--color-background-primary);
  flex-shrink: 0;
}

.search__input {
  flex: 1;
  height: 100%;

  padding: 0 1rem;
  border: none;
  outline: none;

  background: transparent;
  color: var(--color-text-primary);
  font-style: italic;
}

.search__input::placeholder {
  color: var(--color-text-tertiary);
}

.table__table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

.table__head-content {
  display: flex;
  justify-content: start;
  align-items: center;
  width: 100%;
}

.btn--sort {
  background: none;
  border: none;
  cursor: pointer;
  color: inherit;
  padding: 0;
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
  padding: 1rem 0 1rem 0;
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