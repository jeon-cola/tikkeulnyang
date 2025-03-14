import axios from 'axios';
import '/src/index.css'
import { redirect } from 'react-router-dom';

export default function Login() {
    function KakaoLogin() {
        console.log(import.meta.env.VITE_APP_KAKAO_API_KEY)
        window.Kakao.init(import.meta.env.VITE_APP_KAKAO_API_KEY)
        window.Kakao.Auth.authorize({
            redirectUri:"http://localhost:5173/user/"
        })
    }
    return(
        <>
            <button className="longButton" onClick={KakaoLogin}>카카오 로그인</button>
        </>
    )
}