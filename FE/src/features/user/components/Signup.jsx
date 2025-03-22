import axios from "axios";
import "/src/index.css";
import { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import CustomHeader from "../../../components/CustomHeader";

export default function Signup() {
  const [nicknameCheck, setNicknameCheck] = useState(false);
  const [isNickname, setIsNickname] = useState("");
  const [searchParams] = useSearchParams();
  const emailParam = searchParams.get("email");

  const nav = useNavigate();
  // 유저 데이터
  const [user, setUser] = useState({
    nickname: "",
    name: "",
    birthDate: "",
    email: emailParam || "",
  });

  // 입력정보 업데이트
  function inputUpdateHandler(e) {
    const { name, value } = e.target;
    console.log(name, value);
    setUser({
      ...user,
      [name]: value,
    });
  }

  // 닉네임 중복체크 로직
  async function nicknameCheckHandler(e) {
    e.preventDefault();
    try {
      const response = await axios.get(
        "https://j12c107.p.ssafy.io/api/user/check-nickname",
        {
          params: {
            nickname: user.nickname,
          },
        }
      );
      if (response.data.status === "success") {
        // 사용 가능
        setNicknameCheck(true);
        setIsNickname(true); // → true면 "사용가능한 닉네임"
      } else {
        // fail
        setNicknameCheck(true);
        setIsNickname(false); // → false면 "사용중인 닉네임"
      }
    } catch (error) {
      console.log(error);
      setNicknameCheck(false);
      window.alert("서버 에러. 잠시 후 시도해주세요");
    }
  }

  //회원 가입 로직
  async function signUpHandler(e) {
    e.preventDefault();
    try {
      const response = await axios.post(
        "https://j12c107.p.ssafy.io/api/user/register",
        {
          name: user.name,
          nickname: user.nickname,
          email: user.email,
          birthDate: user.birthDate,
        }
      );
      if (response.status == 200) {
        if (response.data.status === "success") {
          window.alert("회원가입에 성공하셨습니다");
          nav("/user");
        }
      } else {
        window.alert("잘못된 요청입니다다");
      }
    } catch (error) {
      console.log(error);
      window.alert("서버 에러. 잠시 후 시도해주세요");
    }
  }

  // 전 항목 입력했는지 체크
  const isFormValid =
    user.nickname && user.name && user.birthDate && nicknameCheck && isNickname;

  return (
    <>
      <CustomHeader title="회원가입" />

      <form className="flex flex-col gap-[30px]">
        {/* 닉네임 */}
        <div className="w-[364px] h-10 flex-none order-none flex-grow-0">
          <input
            type="text"
            placeholder="닉네임을 입력해주세요"
            name="nickname"
            value={user.nickname}
            onChange={inputUpdateHandler}
            onBlur={nicknameCheckHandler}
            className="w-full h-full px-3 rounded-md"
          />
          {nicknameCheck ? (
            !isNickname ? (
              <span className="text-red-500">"사용중인 닉네임입니다"</span>
            ) : (
              <span className="text-green-500">"사용가능한 닉네임입니다"</span>
            )
          ) : (
            ""
          )}
        </div>

        {/* 이름 */}
        <div className="w-[364px] h-10 flex-none order-none flex-grow-0">
          <input
            type="text"
            placeholder="이름을 입력해주세요"
            name="name"
            value={user.name}
            onChange={inputUpdateHandler}
            className="w-full h-full px-3 rounded-md"
          />
        </div>

        {/* 이메일(고정) */}
        <div className="w-[364px] h-10 flex-none order-none flex-grow-0 mb-4 flex items-center px-3 bg-gray-200 rounded-md">
          <p>{user.email}</p>
        </div>

        {/* 생년월일 */}
        <div className="w-[364px] h-10 flex-none order-none flex-grow-0">
          <input
            type="date"
            name="birthDate"
            value={user.birthDate}
            onChange={inputUpdateHandler}
            className="w-full h-full px-3 rounded-md"
          />
        </div>

        {/* 회원가입 버튼 */}
        <button
          className="longButton w-80 h-10 flex-none order-none flex-grow-0 text-center flex items-center justify-center bg-blue-500 text-white rounded-md hover:bg-blue-600 disabled:opacity-50 disabled:bg-gray-400 disabled:cursor-not-allowed mx-auto"
          onClick={signUpHandler}
          disabled={!isFormValid}
        >
          다음
        </button>
      </form>
    </>
  );
}
