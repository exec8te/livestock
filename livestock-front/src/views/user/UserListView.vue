<script lang="ts" setup>
import {computed, onMounted, ref, watch} from "vue";
import * as userService from "@/services/UserService.ts";
import type {UserDto} from "@/dto/user/UserDto.js";

const users = ref<UserDto[]>([]);
const error = ref<string | null>(null);
const loading = ref(false);

// Pagination
const page = ref(0);
const size = ref(20);
const totalElements = ref(0);
const totalPages = ref(0);

// Tri : un seul champ actif à la fois ici, adapte si tu veux du multi-tri
const sortField = ref("createdAt");
const sortDirection = ref<"asc" | "desc">("desc");

// Recherche
const search = ref("");
let debounceTimeout: ReturnType<typeof setTimeout>;

// Sélection
type UserId = UserDto["id"];
const selectedIds = ref<Set<UserId>>(new Set());
const selectAllCheckbox = ref<HTMLInputElement | null>(null);

const allSelected = computed(() =>
    users.value.length > 0 && users.value.every((u) => selectedIds.value.has(u.id))
);

const someSelected = computed(() =>
    users.value.some((u) => selectedIds.value.has(u.id)) && !allSelected.value
);

function isSelected(id: UserId) {
  return selectedIds.value.has(id);
}

function toggleSelectOne(id: UserId) {
  const next = new Set(selectedIds.value);
  if (next.has(id)) {
    next.delete(id);
  } else {
    next.add(id);
  }
  selectedIds.value = next;
}

function toggleSelectAll() {
  if (allSelected.value) {
    // Tout est déjà sélectionné -> on désélectionne tout
    selectedIds.value = new Set();
  } else {
    // On sélectionne tous les utilisateurs de la page courante
    selectedIds.value = new Set(users.value.map((u) => u.id));
  }
}

// Reflète l'état "indéterminé" de la checkbox (impossible via simple binding HTML)
watch([someSelected, users], () => {
  if (selectAllCheckbox.value) {
    selectAllCheckbox.value.indeterminate = someSelected.value;
  }
}, {immediate: true});

async function fetchUsers() {
  loading.value = true;
  try {
    const response = await userService.getAll(
        {search: search.value || undefined},
        {
          page: page.value,
          size: size.value,
          sort: [`${sortField.value},${sortDirection.value}`],
        }
    );

    users.value = response.data.content;
    totalElements.value = response.data.totalElements;
    totalPages.value = response.data.totalPages;
    error.value = null;
  } catch (err: unknown) {
    if (err instanceof Error) {
      error.value = "Impossible de charger les utilisateurs : " + err.message;
    } else {
      error.value = "Impossible de charger les utilisateurs.";
    }
    console.error("Erreur :", err);
  } finally {
    loading.value = false;
  }
}

function toggleSort(field: string) {
  if (sortField.value === field) {
    sortDirection.value = sortDirection.value === "asc" ? "desc" : "asc";
  } else {
    sortField.value = field;
    sortDirection.value = "asc";
  }
  page.value = 0;
}

function sortIcon(field: string) {
  if (sortField.value !== field) return "fa-sort";
  return sortDirection.value === "asc" ? "fa-sort-up" : "fa-sort-down";
}

function clearFilters() {
  search.value = "";
  sortField.value = "createdAt";
  sortDirection.value = "desc";
  page.value = 0;
}

function goToPage(newPage: number) {
  if (newPage < 0 || newPage >= totalPages.value) return;
  page.value = newPage;
}

function deleteUser(id: string) {
  try {
    userService.deleteUserById(
        id
    );
    fetchUsers();
    error.value = null;
  } catch (err: unknown) {
    if (err instanceof Error) {
      error.value = "Impossible de supprimer l'utilisateur : " + id;
    } else {
      error.value = "Impossible de charger l'utilisateur.";
    }
    console.error("Erreur :", err);
  }
}

onMounted(fetchUsers);

// Debounce sur la recherche texte
watch(search, () => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(() => {
    page.value = 0;
    fetchUsers();
  }, 300);
});

// Rechargement immédiat sur changement de page / tri / taille
// On vide aussi la sélection quand la liste change de contenu
watch([page, size, sortField, sortDirection], () => {
  selectedIds.value = new Set();
  fetchUsers();
});
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
          <div class="search">
            <div class="search__icon">
              <i class="fa-solid fa-magnifying-glass"></i>
            </div>

            <input
                v-model="search"
                class="search__input"
                placeholder="Rechercher un utilisateur..."
                type="text"
            />
          </div>

          <button :disabled="search.length === 0"
                  class="tool" @click="clearFilters"><span>Clear</span></button>
        </div>

        <RouterLink
            :to="{ name: 'user-new'}"
            class="tool"
        >
          <i class="fa-solid fa-user-plus"></i><span>Ajouter</span>
        </RouterLink>
      </section>

      <table class="table__table">
        <thead class="table__head">
        <tr class="table__row table__row--head">
          <th class="table__cell table__cell--head table__cell--checkbox">
            <input
                ref="selectAllCheckbox"
                :checked="allSelected"
                class="table__checkbox"
                type="checkbox"
                @click="toggleSelectAll()"
            />
          </th>
          <th class="table__cell table__cell--head">
            <div class="table__head-content">
              <button class="btn--sort" @click="toggleSort('fullName')">
                Full name <i :class="`fa-solid ${sortIcon('fullName')}`"></i>
              </button>
            </div>
          </th>
          <th class="table__cell table__cell--head">
            <div class="table__head-content">
              <button class="btn--sort" @click="toggleSort('email')">
                Email <i :class="`fa-solid ${sortIcon('email')}`"></i>
              </button>
            </div>
          </th>
          <th class="table__cell table__cell--head">
            Role
          </th>
          <th class="table__cell table__cell--head">
            <div class="table__head-content">
              <button class="btn--sort" @click="toggleSort('updatedAt')">
                Updated at <i :class="`fa-solid ${sortIcon('updatedAt')}`"></i>
              </button>
            </div>
          </th>
          <th class="table__cell table__cell--head">
            <div class="table__head-content">
              <button class="btn--sort" @click="toggleSort('createdAt')">
                Created at <i :class="`fa-solid ${sortIcon('createdAt')}`"></i>
              </button>
            </div>
          </th>
          <th class="table__cell table__cell--head">Action</th>
        </tr>
        </thead>

        <tbody class="table__body">
        <tr v-if="users.length === 0">
          <td class="table__cell" colspan="7">Aucun utilisateur trouvé.</td>
        </tr>
        <template v-else>
          <tr
              v-for="user in users"
              :key="user.id"
              :class="{ 'table__row--body-selected': isSelected(user.id) }"
              class="table__row table__row--body"
          >
            <td class="table__cell table__cell--checkbox">
              <input
                  :checked="isSelected(user.id)"
                  class="table__checkbox"
                  type="checkbox"
                  @click="toggleSelectOne(user.id)"
              />
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

                <button class="form-action form-action--delete" @click="deleteUser(user.id)">
                  <i class="fa-solid fa-trash"></i>
                </button>
              </div>
            </td>
          </tr>
        </template>
        </tbody>
      </table>

      <div class="pagination">
        <span class="pagination__info">
          {{ totalElements }} utilisateur(s) — page {{ page + 1 }} / {{ totalPages || 1 }}
        </span>
        <div class="pagination__controls">
          <button :disabled="page === 0" class="tool" @click="goToPage(page - 1)">
            <i class="fa-solid fa-chevron-left"></i>
          </button>
          <button :disabled="page + 1 >= totalPages" class="tool" @click="goToPage(page + 1)">
            <i class="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
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
  text-decoration: none;
}

.tool[disabled] {
  cursor: not-allowed;
  background: var(--color-background-input-disabled);
}

.tool[disabled]:hover {
  background: var(--color-background-input-disabled);
  color: var(--color-text-primary);
  border: 1px solid var(--color-border-primary);
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
  width: 2rem;
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

.table__row--body-selected,
.table__row--body:hover {
  background: var(--color-background-row-selected);
}

.table__row--body:last-child {
  border-bottom: none;
}

.table__cell {
  padding: 1rem;
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

.table__cell:last-child {
  text-align: center;
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

.table__cell--email, .table__cell--date {
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

.table__badge--admin, .table__badge--customer {
  background: var(--color-background-link-primary);
  color: var(--color-text-secondary);
}

.table__badge--manager, .table__badge--personal {
  background: rgb(255 178 102 / 0.24);
  color: #c9740a;
}

.table__badge--user, .table__badge--employees {
  background: rgb(66 184 131 / 0.16);
  color: var(--color-success-primary);
}

.table__actions {
  display: flex;
  align-items: center;
  justify-content: end;
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
  transition: background-color .2s ease, color .2s ease, transform .15s ease;
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

.pagination {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
  font-size: .85rem;
  color: var(--color-text-tertiary);
}

.pagination__controls {
  display: flex;
  gap: .5rem;
}

.pagination__controls .tool:disabled {
  opacity: .4;
  cursor: not-allowed;
}
</style>