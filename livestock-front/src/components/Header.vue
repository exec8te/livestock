<script setup>
import {ref} from "vue";
import {useAuthStore} from "@/stores/auth";
import {storeToRefs} from "pinia";
import HeaderTop from "@/components/HeaderTop.vue";
import NavGroup from "@/components/NavGroup.vue";
import UserProfile from "@/components/UserProfile.vue";

const authStore = useAuthStore();
const {user} = storeToRefs(authStore);

const groups = ref([
  {
    title: "Favoris",
    isOpen: true,
    links: [
      {to: "/", label: "Dashboard", icon: "fa-solid fa-square-poll-vertical"},
      {to: "/users", label: "Users", icon: "fa-solid fa-users"},
    ]
  },
  {
    title: "General",
    isOpen: false,
    links: [
      {to: "/", label: "Dashboard", icon: "fa-solid fa-square-poll-vertical"},
      {to: "/", label: "Products", icon: "fa-solid fa-box-open"},
      {to: "/", label: "Companies", icon: "fa-solid fa-industry"},
    ]
  },
  {
    title: "Users",
    isOpen: false,
    links: [
      {to: "/users", label: "Users", icon: "fa-solid fa-users"},
      {to: "/", label: "Roles", icon: "fa-solid fa-user-shield"},
      {to: "/", label: "Permissions", icon: "fa-solid fa-user-gear"},
    ]
  },
]);

const accountOpen = ref(false);

const accountLinks = [
  {to: "/", label: "Profil", icon: "fa-solid fa-user"},
  {to: "/", label: "Accessibility", icon: "fa-solid fa-universal-access"},
  {to: "/users", label: "Logout", icon: "fa-solid fa-right-from-bracket"},
];

const toggleGroup = (index) => {
  const isCurrentlyOpen = groups.value[index].isOpen;
  groups.value.forEach((group) => group.isOpen = false);
  groups.value[index].isOpen = !isCurrentlyOpen;
  accountOpen.value = false;
};

const toggleAccount = () => {
  accountOpen.value = !accountOpen.value;
};
</script>

<template>
  <header class="header">
    <HeaderTop/>

    <nav class="header__navbar">
      <div class="navbar__links-container">
        <NavGroup
            v-for="(group, index) in groups"
            :key="index"
            :is-open="group.isOpen"
            :links="group.links"
            :title="group.title"
            @toggle="toggleGroup(index)"
        />
      </div>
    </nav>

    <div class="header__footer">
      <NavGroup
          :is-open="accountOpen"
          :links="accountLinks"
          icon-direction="up"
          reverse
      >
        <template #trigger>
          <UserProfile
              :is-open="accountOpen"
              :user="user"
              @toggle="toggleAccount"
          />
        </template>
      </NavGroup>
    </div>
  </header>
</template>

<style scoped>
.header {
  display: flex;
  flex-direction: column;
  justify-content: start;
  align-items: center;
  height: 100vh;
  width: 300px;
  background: var(--color-background-primary);
  color: var(--color-text-primary);
  padding: 1rem 1rem 2rem 1rem;
  border-right: 1px solid var(--color-border-primary);
}

.header__navbar {
  display: flex;
  flex-direction: column;
  align-items: start;
  width: 100%;
  margin-top: 1rem;
  flex: 1;
}

.navbar__links-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: .5rem;
}

.header__footer {
  width: 100%;
  margin-top: auto;
  display: flex;
  flex-direction: column;
  border-radius: 0.5rem;
}
</style>
