import axios from "axios";

const Api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

export default Api;

// 응답 인터셉터
Api.interceptors.response.use();
