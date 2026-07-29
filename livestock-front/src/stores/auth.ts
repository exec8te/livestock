import {defineStore} from "pinia";
import * as authService from "@/services/AuthService";
import type {UserDto} from "@/dto/user/UserDto.ts";
import type {LoginDto} from "@/dto/auth/LoginDto.ts";
import {getMe} from "@/services/UserService";
import axios from "axios";

export const useAuthStore = defineStore("auth", {
    state: () => ({
        user: null as UserDto | null,
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
            } catch (error) {
                this.user = null;
                this.isAuthenticated = false;

                if (axios.isAxiosError(error) && error.response?.status === 401) {
                    // Déjà géré par l'intercepteur si nécessaire, 
                    // mais on s'assure que l'état est cohérent
                }
            } finally {
                this.initialized = true;
            }
        },

        async login(login: LoginDto) {
            try {
                await authService.login(login);
                await this.fetchUser();
            } catch (error) {
                this.user = null;
                this.isAuthenticated = false;
                throw error;
            }
        },

        async logout() {
            this.user = null;
            this.isAuthenticated = false;
            this.initialized = true;

            try {
                await authService.logout();
            } catch (error) {
                console.error("Erreur lors de la déconnexion", error);
            }
        }
    }
});