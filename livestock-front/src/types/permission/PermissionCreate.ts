import type {PermissionAction} from "@/types/permission/PermissionAction.ts";

export interface PermissionCreate {

    resource: string;
    action: typeof PermissionAction;
}