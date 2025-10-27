export interface User {
    id: string;
    name: string;
    email: string;
}

export interface Order {
    id: string;
    userId: string;
    product: string;
    quantity: number;
}

export interface ApiResponse<T> {
    data: T;
    error?: string;
}

export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';