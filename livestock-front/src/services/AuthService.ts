import axios from "@/plugins/axios";
import type {LoginDto} from "@/dto/auth/LoginDto.ts";
import type {RegisterDto} from "@/dto/auth/RegisterDto.ts";
import router from "@/router";

export function login(data: LoginDto) {
    return axios.post("/auth/login", data);
}

export function register(data: RegisterDto) {
    return axios.post("/auth/register", data);
}

export async function logout() {
    try {
        await axios.post('/api/logout', {}, {
            withCredentials: true
        });
    } catch (error) {
        console.error('Erreur lors du logout', error);
    } finally {
        // Redirection même si l'API est indisponible
        router.push('/login');
    }
}