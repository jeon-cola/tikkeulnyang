import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import "/src/index.css";
import Api from "../../../services/Api";
import Cat from "../assets/cream_cat.png";
import SmileCat from "../assets/smile_cat.png";
import Coin from "../assets/coin.png";

export default function Login() {
  const { isAuthenticated } = useSelector((state) => state.user);
  const nav = useNavigate();
  const [isSmile, setIsSmile] = useState(false); // 웃는 고양이 상태

  useEffect(() => {
    if (isAuthenticated) {
      nav("/home");
    }
  }, [isAuthenticated]);

  function KakaoLogin() {
    window.location.href = `${Api.defaults.baseURL}/api/auth/login`;
  }

  if (isAuthenticated) return null;

  return (
    <div className="w-full">
      {/* "티끌냥" 텍스트로고+고양이+코인 */}
      <div className="relative w-full h-[240px] flex flex-col items-center justify-start">
        {/* 동전 - 텍스트 위에서 고양이까지 빠르게 떨어짐 */}
        <img
          src={Coin}
          alt="동전"
          className="w-[40px] h-[40px] absolute z-0 animate-dropCoin"
          style={{ top: "20px" }}
          onAnimationEnd={() => setIsSmile(true)} // 애니메이션 끝나면 고양이 변경
        />

        {/* 텍스트 + 로고 */}
        <div className="flex items-center justify-center mt-4 mb-6 z-10">
          <h1 className="mr-2 text-[60px]">티끌냥</h1>
          <img src="/logo.png" alt="티끌냥 로고" />
        </div>

        {/* 고양이 이미지 */}
        <img
          src={isSmile ? SmileCat : Cat}
          alt="고양이 캐릭터"
          className="w-[160px] h-auto z-10 mt-22"
        />
      </div>

      <div className="flex items-center justify-center w-full my-8 mt-28">
        <div className="h-[1px] bg-black w-[102px]"></div>
        <p className="mx-4 text-[20px] font-extralight leading-6">SNS LOGIN</p>
        <div className="h-[1px] bg-black w-[104px]"></div>
      </div>

      <div
        onClick={KakaoLogin}
        className="flex justify-center items-center w-full"
      >
        <img src="/kakao_login.png" alt="카카오 로그인" />
      </div>

      <style>{`
        @keyframes dropCoin {
          0% {
            transform: translateY(0);
            opacity: 0;
          }
          100% {
            transform: translateY(150px);
            opacity: 1;
          }
        }

        .animate-dropCoin {
          animation: dropCoin 0.6s ease-out forwards;
        }
      `}</style>
    </div>
  );
}
