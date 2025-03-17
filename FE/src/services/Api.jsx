import axios from "axios";

const Api = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true
});

export default Api;

<<<<<<< HEAD
=======
// 응답 인터셉터
Api.interceptors.response.use(

)
>>>>>>> 585e527 (Feat: 수정)
