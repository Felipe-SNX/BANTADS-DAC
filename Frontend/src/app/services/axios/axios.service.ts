import { Injectable } from '@angular/core';
import axios from 'axios';

const API_URL_BASE = 'http://localhost:3000';

@Injectable({
  providedIn: 'root'
})
export class AxiosService {

  private axiosInstance;

  constructor() {
    this.axiosInstance = axios.create({
      baseURL: API_URL_BASE,
      timeout: 5000,
      headers: {
        'Content-Type': 'application/json'
      }
    });
    /*this.axiosInstance.interceptors.request.use(config => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    });*/
  }

  async get<T>(url: string): Promise<T> {
    const response = await this.axiosInstance.get<T>(url);
    return response.data;
  }

  async post<T>(url: string, body: any): Promise<T> {
    const response = await this.axiosInstance.post<T>(url, body);
    return response.data;
  }

  async put<T>(url: string, body: any): Promise<T> {
    const response = await this.axiosInstance.put<T>(url, body);
    return response.data;
  }

  async delete<T>(url: string): Promise<T> {
    const response = await this.axiosInstance.delete<T>(url);
    return response.data;
  }
}
