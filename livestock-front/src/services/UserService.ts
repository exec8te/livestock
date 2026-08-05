import axios from "@/plugins/axios";
import type {UserDto} from "@/dto/user/UserDto.ts";
import type {UserUpdatePasswordDto} from "@/dto/user/UserUpdatePasswordDto.ts";
import type {UserUpdateAdminDto} from "@/dto/user/UserUpdateAdminDto.ts";
import type {UserUpdateDto} from "@/dto/user/UserUpdateDto.ts";
import type {PageResponseDto} from "@/dto/page/PageResponseDto.ts";
import type {UserFilterDto} from "@/dto/user/UserFilterDto.ts";

export interface PageParams {
    page?: number;
    size?: number;
    sort?: string[]; // ex: ["createdAt,desc", "fullName,asc"]
}

export function getAll(filter: UserFilterDto = {}, page: PageParams = {}) {
    return axios.get<PageResponseDto<UserDto>>("/users", {
        params: {...filter, ...page},
    });
}

export function getMe() {
    return axios.get<UserDto>("/users/me");
}

export function updateMe(user: UserUpdateDto) {
    return axios.put("/users/me", user);
}

export function updateMePassword(user: UserUpdatePasswordDto) {
    return axios.put("/users/me/password", user);
}

export function getUserById(id: number) {
    return axios.get<UserDto>(`/users/${id}`);
}

export function updateUserAdmin(id: number, user: UserUpdateAdminDto) {
    return axios.put(`/users/${id}`, user);
}

export function deleteUserById(id: number) {
    return axios.delete(`/users/${id}`);
}