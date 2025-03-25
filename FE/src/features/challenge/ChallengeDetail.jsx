import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import CustomHeader from "@/components/CustomHeader";
import ChallengeDetailImg from "@/features/challenge/components/ChallengeDetailImg";
import ChallengeIntro from "@/features/challenge/components/ChallengeIntro";
import MyCurrentStatus from "@/features/challenge/components/MyCurrentStatus";
import ParticiStatics from "./components/ParticiStatics";
import { useNavigate } from "react-router-dom";
import { ChallengeService } from "@/features/challenge/servies/ChallengeService";

/*
  추후에 axios로 채워넣을 데이터: 
  title, imageInfo, challengeType, challengeName, currentParticipants, startDate, endDate,
   챌린지 상세 설명,
   deposit, currentProgress, 
*/
export default function ChallengeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/challenge/enter/${id}`, {
      state: {
        challengeData: currChallenge,
      },
    });
  };
  const [currChallenge, setCurrChallenge] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const response = await ChallengeService.getCurrChallenge(id);
      console.log(response.data);
      setCurrChallenge(response.data);
      setIsLoading(false);
    } catch (error) {
      console.error(error);
      setIsLoading(false);
      throw error;
    }
  };
  // 페이지가 실행될 때 현재 보고자 하는 챌린지의 상세 내용을 가져온다.
  useEffect(() => {
    fetchData();
  }, []);

  return (
    <>
      <CustomHeader title="챌린지 상세" />
      <div className="flex flex-col items-start p-[30px_20px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        {isLoading ? (
          <></>
        ) : (
          <>
            {/* 챌린지 상세 이미지 */}
            <ChallengeDetailImg imageInfo={currChallenge.images[0].url} />
            <ChallengeIntro
              challengeType={currChallenge.challengeType}
              challengeName={currChallenge.challengeName}
              currentParticipants={currChallenge.participantCount}
              startDate={currChallenge.startDate}
              endDate={currChallenge.endDate}
            />

            {/* 챌린지 상세 설명 */}
            <div className="flex flex-col items-center p-[12px_11px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
              <div className="whitespace-pre-line text-left w-full h-auto font-normal text-[17px] leading-[20px] text-black flex-none order-0 flex-grow-0">
                <p>{currChallenge.description}</p>
              </div>
            </div>

            {/* 챌린지 주의사항 */}
            <div className="flex flex-col items-center p-[12px_11px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
              <div className="whitespace-pre-line text-left w-full h-auto font-normal text-[17px] leading-[20px] text-black flex-none order-0 flex-grow-0">
                <h2 className="text-lg font-bold mb-1">주의사항</h2>

                <p>
                  {`
                  챌린지 시작 전까지 100% 환불

                  챌린지 시작 후부터 환불 불가

                  참가비용 최소 ${currChallenge.limitAmount}원
                  `}
                </p>
              </div>
            </div>

            {/* 현재 챌린지들의 현황*/}
            <MyCurrentStatus
              deposit={currChallenge.limitAmount}
              currentProgress={currChallenge.achievementRate}
            />

            <ParticiStatics
              participantCount={currChallenge.participantCount}
              averageExpectedSuccessRate={
                currChallenge.averageExpectedSuccessRate
              }
            />
            {/* 참가 버튼 */}
            <div className="w-full justify-center flex flex-row">
              <button className="longButton" onClick={handleClick}>
                참여하기
              </button>
            </div>
          </>
        )}
      </div>
    </>
  );
}
