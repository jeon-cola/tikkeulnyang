import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";

export default function ChallengeWidget({
  challengeId = "1",
  thumbnailUrl = "undefined",
  challengeName = "undefined",
  endDate = "undefined",
}) {
  const navigate = useNavigate();
  const [remainDate, setRemainDate] = useState("");

  useEffect(() => {
    // prop으로 받은 endDate와 현재 날짜의 차이를 구해, remainDate에 저장
    const endDateObj = new Date(endDate);
    const currentDate = new Date();
    const diffTime = endDateObj.getTime() - currentDate.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    setRemainDate(diffDays);
  }, [endDate]);

  return (
    <>
      <div
        className="flex flex-col p-[12px_20px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]"
        style={{
          backgroundImage: `url(${thumbnailUrl})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          backgroundRepeat: "no-repeat",
        }}
        onClick={() => {
          navigate(`/challenge/${challengeId}`);
        }}
      >
        <div className="absolute inset-0 bg-black/25 backdrop-blur-xs rounded-[6px]"></div>

        <div className="z-10">
          <h2 className="pt-[18px] font-semibold text-xl text-left leading-[17px] tracking-[0.01em] text-white mb-[6px]">
            {challengeName} 챌린지
          </h2>

          <div className="font-light text-lg text-right pt-24 leading-7 tracking-[0.01em] text-white">
            챌린지 종료까지 {remainDate}일
          </div>

          {/* <div className="pt-[99px] w-full justify-center flex flex-row"></div> */}
        </div>
      </div>
    </>
  );
}
