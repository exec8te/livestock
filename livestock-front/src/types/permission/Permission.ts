import type {PermissionAction} from "@/types/permission/PermissionAction.ts";

export interface Permission {

    id: number;
    resource: string;
    action: typeof PermissionAction;
}