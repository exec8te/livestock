import {createRouter, createWebHistory, type RouteRecordRaw} from "vue-router";

import UserListView from "@/views/user/UserListView.vue";
import AuthFormView from "@/views/AuthFormView.vue";

import {useAuthStore} from "@/stores/auth";
import UserDetailView from "@/views/UserDetailView.vue";

const routes: RouteRecordRaw[] = [
    {
        path: "/",
        name: "root",
        component: UserDetailView
    },
    {
        path: "/login",
        name: "login",
        component: AuthFormView,
    },
    {
        path: "/logout",
        name: "logout",
        component: AuthFormView,
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