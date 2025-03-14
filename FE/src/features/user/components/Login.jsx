
import '/src/index.css'

export default function Login() {
    function KakaoLogin() {
        window.location.href = "http://localhost:8080/api/auth/login"
    }
    
    return(
        <>
            <button className="longButton" onClick={KakaoLogin}>카카오 로그인</button>
        </>
    )
}