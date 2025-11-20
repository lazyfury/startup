import axios, { type AxiosInstance, type AxiosResponse } from 'axios';

const baseURL = import.meta.env.VITE_HQJH_BASE_URL || '/';

const instance: AxiosInstance = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
  timeout: 15000,
});

instance.interceptors.request.use((config) => {
  return config;
});

instance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    const msg = error?.response?.data?.message || error?.message || '请求错误';
    return Promise.reject(new Error(msg));
  }
);


export default instance;