import axios from "axios";
import {useAuthStore} from "@/stores/auth.ts";

const api = axios.create({
    baseURL: "http://localhost:8080", // ex: http://localhost:8080
    withCredentials: true, // si tu utilises des cookies (JWT en cookie httpOnly par ex.)
    paramsSerializer: {
        indexes: null, // sort=a&sort=b au lieu de sort[0]=a&sort[1]=b
    },
});

// Intercepteur requête : ajoute le token si tu es en Bearer token (localStorage/pinia)
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token"); // ou depuis ton store Pinia
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Intercepteur réponse : gestion centralisée des erreurs (401, 403, etc.)
api.interceptors.response.use(
    response => response,

    error => {

        if (error.response?.status === 401) {

            const authStore = useAuthStore();

            authStore.logout();

            window.location.href = "/login";
        }

        return Promise.reject(error);
    }
);

export default api;