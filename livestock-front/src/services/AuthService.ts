import api from "@/utils/api";
import type {LoginDto} from "@/dto/auth/LoginDto.ts";
import type {RegisterDto} from "@/dto/auth/RegisterDto.ts";

export function login(data: LoginDto) {
    return api.post("/auth/login", data);
}

export function register(data: RegisterDto) {
    return api.post("/auth/register", data);
}

export function logout() {
    return api.post("/auth/logout");
}