import { useState } from "react"
import userProfile from "/userProfile.png"

export default function MyPageMenu() {
  const [userImage, setUserImage] = useState(userProfile);
  const [userName, setUserName] = useState("길동홍");
  const [userLevel, setUserLevel] = useState("LV.1");
  const [userEmail, setUserEmail] = useState("abc@naver.com")

  return(
    <div className="flex flex-col justify-center gap-4  w-screen max-w-none mx-0 px-0">

      {/* 유저 정보 */}
      <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 border border-dashed border-blue-200">
        <div className="mr-4">
          <img src={userImage} alt="유저 이미지"/>
        </div>
        <div className="flex flex-col">
          <div className="flex items-center mb-1 gap-3">
            <span>{userName}</span>
            <span>{userLevel}</span>
          </div>
          <div>
            <span>{userEmail}</span>
          </div>
        </div>
      </div>
    </div>
  )
}