import {defineStore} from "pinia";
import * as authService from "@/services/AuthService";
import type {User} from "@/types/user/User.ts";
import type {Login} from "@/types/auth/Login.ts";
import {getMe} from "@/services/UserService";

export const useAuthStore = defineStore("auth", {
    state: () => ({
        user: null as User | null,
        isAuthenticated: false,
        initialized: false,
    }),
    
    persist: true,

    actions: {
        async fetchUser() {
            try {
                const response = await getMe();

                this.user = response.data;
                this.isAuthenticated = true;
            } catch {
                this.user = null;
                this.isAuthenticated = false;
            } finally {
                this.initialized = true;
            }
        },

        async login(login: Login) {
            await authService.login(login);
            await this.fetchUser();
        },

        async logout() {
            this.user = null;
            this.isAuthenticated = false;
            this.initialized = true;
            
            await authService.logout();
        }
    }
});