import axios from "@/plugins/axios";
import type {LoginDto} from "@/dto/auth/LoginDto.ts";
import type {RegisterDto} from "@/dto/auth/RegisterDto.ts";

export function login(data: LoginDto) {
    return axios.post("/auth/login", data);
}

export function register(data: RegisterDto) {
    return axios.post("/auth/register", data);
}

export function logout() {
    return axios.post("/auth/logout");
}