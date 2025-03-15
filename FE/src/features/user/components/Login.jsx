
import '/src/index.css'

export default function Login() {
    function KakaoLogin() {
        window.location.href = "http://localhost:8080/api/auth/login"
    }
    
    return(
        <>  
            <div className="w-[392px] h-[326px] flex-none order-none self-stretch flex-grow-0 z-0 flex items-center justify-center">
                <h1 className="mr-2 text-[60px]">티끌냥</h1>
                <img src="public/logo.png" alt="티끌냥 로고" />
            </div>

            {/* 이미지 추가 필요 */}
            <div>
                <img src="" alt="" />
            </div>
            
            <div className="flex items-center justify-center w-full my-8">
                <div className="h-[1px] bg-black w-[102px]"></div>
                    <p className="mx-4 text-[20px] font-extralight leading-6">SNS LOGIN</p>
                <div className="h-[1px] bg-black w-[104px]"></div>
            </div>

            <div onClick={KakaoLogin} className="flex justify-center items-center w-full">
                <img src="/kakao_login.png" alt="카카오 로그인" />
            </div>
        </>
    )
}