import type {PermissionAction} from "@/types/permission/PermissionAction.ts";

export interface PermissionUpdate {

    resource: string;
    action: typeof PermissionAction;
}