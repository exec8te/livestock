import api from "@/utils/api.ts";
import type {UserDto} from "@/dto/user/UserDto.ts";
import type {UserUpdatePasswordDto} from "@/dto/user/UserUpdatePasswordDto.ts";
import type {UserUpdateAdminDto} from "@/dto/user/UserUpdateAdminDto.ts";
import type {UserUpdateDto} from "@/dto/user/UserUpdateDto.ts";
import type {PageResponse} from "@/dto/PageResponse.ts";

export function getMe() {
    return api.get<UserDto>("/users/me");
}

export function updateMe(user: UserUpdateDto) {
    return api.put("/users/me", user);
}

export function updateMePassword(user: UserUpdatePasswordDto) {
    return api.put('/users/me/password', user);
}

export function getAll() {
    return api.get<PageResponse<UserDto>>("/users");
}

export function getUserById(id: number) {
    return api.get<UserDto>(`/users/${id}`);
}

export function updateUserAdmin(id: number, user: UserUpdateAdminDto) {
    return api.put(`/users/${id}`, user);
}


export function deleteUserById(id: number) {
    return api.delete(`/users/${id}`);
}
