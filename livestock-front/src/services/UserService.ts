import api from "@/utils/api.ts";
import type {User} from "@/types/user/User.ts";
import type {UserUpdatePassword} from "@/types/user/UserUpdatePassword.ts";
import type {UserUpdateAdmin} from "@/types/user/UserUpdateAdmin.ts";
import type {UserUpdate} from "@/types/user/UserUpdate.ts";
import type {PageResponse} from "@/types/PageResponse.ts";

export function getMe() {
    return api.get<User>("/users/me");
}

export function updateMe(user: UserUpdate) {
    return api.put("/users/me", user);
}

export function updateMePassword(user: UserUpdatePassword) {
    return api.put('/users/me/password', user);
}

export function getAll() {
    return api.get<PageResponse<User>>("/users");
}

export function getUserById(id: number) {
    return api.get<User>(`/users/${id}`);
}

export function updateUserAdmin(id: number, user: UserUpdateAdmin) {
    return api.put(`/users/${id}`, user);
}


export function deleteUserById(id: number) {
    return api.delete(`/users/${id}`);
}
