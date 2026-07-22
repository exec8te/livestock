export interface User {

    id: number | null;
    fullName: string;
    email: string;
    role: string;
    createdAt: Date;
    updatedAt: Date | null;
    lastModifiedAt: Date | null;
}