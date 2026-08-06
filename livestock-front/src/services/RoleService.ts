import axios from "@/plugins/axios";
import type {RoleDto} from "@/dto/role/RoleDto.ts";
import type {PageResponseDto} from "@/dto/page/PageResponseDto.ts";
import type {RoleFilterDto} from "@/dto/role/RoleFilterDto.ts";
import type {RoleCreateDto} from "@/dto/role/RoleCreateDto.ts";
import type {PageParamsDto} from "@/dto/page/PageParamsDto.ts";
import type {RoleUpdateDto} from "@/dto/role/RoleUpdateDto.ts";


export function getAll(filter: RoleFilterDto = {}, page: PageParamsDto = {}) {
    return axios.get<PageResponseDto<RoleDto>>("/roles", {
        params: {...filter, ...page},
    });
}

export function getAllForUsers(filter: RoleFilterDto = {}) {
    return axios.get<PageResponseDto<RoleDto>>("/roles", {
        params: {...filter},
    });
}

export function getRoleById(id: string) {
    return axios.get<RoleDto>(`/roles/${id}`);
}

export function updateRole(id: string, Role: RoleUpdateDto) {
    return axios.put(`/roles/${id}`, Role);
}

export function deleteRoleById(id: string) {
    return axios.delete(`/roles/${id}`);
}

export function createRole(Role: RoleCreateDto) {
    return axios.post("/roles", Role);
}