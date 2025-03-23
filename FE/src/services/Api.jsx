import axios from "axios";

const Api = axios.create({
  //baseURL: "http://localhost:8080/api",
  baseURL: "http://localhost:3001",
  withCredentials: false, // json-server는 credentials가 필요없으므로 false로 설정
  headers: {
    "Content-Type": "application/json",
  },
});

export default Api;
// 응답 인터셉터
Api.interceptors.response.use();
