import axios from "axios";

const Api = axios.create({
  baseURL: "https://j12c107.p.ssafy.io",
  withCredentials: true,
});

export default Api;

// 응답 인터셉터
Api.interceptors.response.use();
