export interface PageParamsDto {
    page?: number;
    size?: number;
    sort?: string[]; // ex: ["createdAt,desc", "fullName,asc"]
}