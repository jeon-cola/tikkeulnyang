import { useState } from "react"
import { Link } from "react-router-dom";
import userProfile from "/userProfile.png"
import CorrectionIcon from "../assets/CorrectionIcon";
import MyCatIcon from "../assets/MyCatIcon";
import AccountIcon from "../assets/AccountIcon";
import CardIcon from "../assets/CardIcon";
import ReportIcon from "../assets/ReportIcon";
import "../../../components/CustomHeader"
import CustomHeader from "../../../components/CustomHeader";
import YellowCat from "../../../../public/YellowCat.jsx";

export default function MyPageMenu() {
  const [userImage, setUserImage] = useState(userProfile);
  const [userName, setUserName] = useState("길동홍");
  const [userLevel, setUserLevel] = useState("LV.1");
  const [userEmail, setUserEmail] = useState("abc@naver.com")
  const [pendingChallenges, setPendingChallenges] = useState(0)
  const [activeChallenges, setActiveChallenges] = useState(0)
  const [completedChallenges, setCompletedChallenges] = useState(0)

  return(
      <div className="flex flex-col justify-center gap-5 min-w-[345px]">

        <CustomHeader title="마이 페이지"/>
        
        {/* 유저 정보 */}
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4">
          <div className="mr-4">
            <img src={userImage} alt="유저 이미지"/>
          </div>
          <div className="flex flex-col">
            <div className="flex items-center mb-1 gap-3">
              <span className="font-semibold">{userName}</span>
              <span className="font-semibold text-xs">{userLevel}</span>
            </div>
            <div>
              <span className="font-regular text-x">{userEmail}</span>
            </div>
          </div>
        </div>

          {/* 예치금 */}
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4">

        </div>

        {/* 챌린지 현황 */}
        <div className="relative">
          <p className="text-left font-semibold">첼린지 현황</p>
          
          {/* 박스 */}
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[25px] flex justify-between items-center p-4 mt-2">
            <div className="flex flex-col items-center w-1/3">
              <p className="text-xl font-medium">{pendingChallenges}</p>
              <p className="text-sm text-gray-500">시작 전</p>
            </div>

            <div className="flex flex-col items-center w-1/3">
              <p className="text-xl font-medium text-red-500">{activeChallenges}</p>
              <p className="text-sm text-gray-500">진행 중</p>
            </div>

            <div className="flex flex-col items-center w-1/3">
              <p className="text-xl font-medium">{completedChallenges}</p>
              <p className="text-sm text-gray-500">완료</p>
            </div>
          </div>
          
          {/* 오른쪽 위에 고정된 YellowCat */}
          <div className="absolute -top-4 right-4 z-10">
            <YellowCat/>
          </div>
        </div>

        <div className="flex flex-col gap-4">
          {/* 회원정보 수정 */}
          <Link to="correction">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
              <div className="mr-4">
                <CorrectionIcon/>
              </div>
                <div className="flex items-center mb-1 gap-3">
                  <p className="font-regular">회원정보 수정</p>
                </div>
            </div>
          </Link>

          {/* 내 고양이 */}
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
            <div className="mr-4">
                <MyCatIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">내 고양이</p>
              </div>
          </div>

          {/* 등록 계좌 설정 */}
          <Link to="account">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
              <div className="mr-4">
                <AccountIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">등록 계좌 설정</p>
              </div>
            </div>
          </Link>

          {/* 등록 카드 설정 */}
          <Link to="card">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
              <div className="mr-4">
                <CardIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">등록 카드 설정</p>
              </div>
            </div>
          </Link>

          {/* 출석부 리포트 */}
          <Link to="report">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 flex items-center p-4">
              <div className="mr-4">
                <ReportIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">출석부 리포트</p>
              </div>
            </div>
          </Link>


        </div>



      </div>

      

  )
}