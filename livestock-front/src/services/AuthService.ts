import api from "@/utils/api";
import type {Login} from "@/types/auth/Login.ts";

export function login(data: Login) {
    return api.post("/auth/login", data);
}

export function register() {
    return api.post("/auth/register");
}

export function logout() {
    return api.post("/auth/logout");
}