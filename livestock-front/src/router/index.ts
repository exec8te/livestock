import {createRouter, createWebHistory, type RouteRecordRaw} from "vue-router";

import UserListView from "@/views/user/UserListView.vue";

import {useAuthStore} from "@/stores/auth";
import UserDetailView from "@/views/UserDetailView.vue";
import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import UserCreateView from "@/views/user/UserCreateView.vue";
import UserUpdateView from "@/views/user/UserUpdateView.vue";

const routes: RouteRecordRaw[] = [
    {
        path: "/",
        name: "root",
        component: UserDetailView
    },
    {
        path: "/login",
        name: "login",
        component: LoginView,
    },
    {
        path: "/register",
        name: "register",
        component: RegisterView,
    },
    {
        path: "/users",
        name: "users",
        component: UserListView,
        meta: {
            requiresAuth: true,
        },
    },
    {
        path: "/users/:id",
        name: "user-edit",
        component: UserUpdateView,
    },
    {
        path: "/users/new",
        name: "user-new",
        component: UserCreateView,
    },
    {
        path: "/profil",
        name: "profil",
        component: UserDetailView,
        meta: {
            requiresAuth: true,
        },
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach(async (to) => {
    const auth = useAuthStore();

    if (!auth.initialized) {
        await auth.fetchUser();
    }

    if (to.meta.requiresAuth && !auth.isAuthenticated) {
        return {name: "login"};
    }

    return true;
});

export default router;