import type {Permission} from "@/types/permission/Permission.ts";

export interface Role {

    id: number;
    name: string;
    isDefault: boolean;
    permissions: Permission[];
    createdAt: Date;
    updatedAt: Date;
}