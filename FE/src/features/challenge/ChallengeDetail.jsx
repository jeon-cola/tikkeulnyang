import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import CustomHeader from "@/components/CustomHeader";
import ChallengeDetailImg from "@/features/challenge/components/ChallengeDetailImg";
import ChallengeIntro from "@/features/challenge/components/ChallengeIntro";
import MyCurrentStatus from "@/features/challenge/components/MyCurrentStatus";
import ParticiStatics from "./components/ParticiStatics";
import { useNavigate } from "react-router-dom";
import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import { ChallengeUtils } from "@/features/challenge/utils/ChallengeUtils";
import CustomBackHeader from "@/components/CustomBackHeader";
import CreateButton from "./components/CreateButton";
import { useSelector } from "react-redux";

/*
  추후에 axios로 채워넣을 데이터: 
  title, imageInfo, challengeType, challengeName, currentParticipants, startDate, endDate,
   챌린지 상세 설명,
   deposit, currentProgress, 
*/
export default function ChallengeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const userEmail = useSelector((state) => state.user.email);

  const handleClick = () => {
    navigate(`/challenge/enter/${id}`, {
      state: {
        challengeData: currChallenge,
      },
    });
  };

  const handleCancel = () => {
    ChallengeService.postChallengeCancel(id);
    navigate("/challenge");
  };

  const handleDelete = () => {
    // 챌린지 삭제 로직 구현
    ChallengeService.deleteChallenge(id);
    navigate("/challenge");
  };

  const [currChallenge, setCurrChallenge] = useState({
    challenge: {
      challengeId: 0,
      challengeName: "",
      challengeType: "",
      targetAmount: 0,
      startDate: "",
      endDate: "",
      description: "",
      createdBy: "",
      activeFlag: false,
      challengeCategory: "",
      createdAt: "",
      maxParticipants: 0,
      limitAmount: 0,
      thumbnailUrl: "",
    },
    participantCount: 0,
    bucketOver100: 0,
    bucket100to85: 0,
    bucket84to50: 0,
    bucket49to25: 0,
    bucket24to0: 0,
    averageSuccessRate: 0.0,
    mySpendingAmount: 3300,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [isExpired, setIsExpired] = useState(false);
  const [isNotStarted, setIsNotStarted] = useState(false);
  const [isParticipated, setIsparticipated] = useState(false);
  const [isOwner, setIsOwner] = useState(false);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const response = await ChallengeService.getCurrChallenge(id);
      console.log("original response", response.data);

      // 날짜 형식 변경
      const formattedData = {
        ...response.data,
        challenge: {
          ...response.data.challenge,
          startDate: ChallengeUtils.formatDate(
            response.data.challenge.startDate
          ),
          endDate: ChallengeUtils.formatDate(response.data.challenge.endDate),
          limitAmount: response.data.challenge.limitAmount.toLocaleString(),
        },
        bucketOver100: Math.round(response.data.bucketOver100 * 100) / 100,
        bucket100to85: Math.round(response.data.bucket100to85 * 100) / 100,
        bucket84to50: Math.round(response.data.bucket84to50 * 100) / 100,
        bucket49to25: Math.round(response.data.bucket49to25 * 100) / 100,
        bucket24to0: Math.round(response.data.bucket24to0 * 100) / 100,
        averageSuccessRate:
          Math.round(response.data.averageSuccessRate * 100) / 100,
      };

      // 종료일이 현재 날짜보다 이전인지 확인
      const endDate = new Date(response.data.challenge.endDate);
      const today = new Date();
      setIsExpired(endDate < today);

      console.log("formatted response", formattedData);

      setCurrChallenge(formattedData);

      // 사용자가 챌린지 작성자인지 확인
      if (userEmail === response.data.challenge.createdBy) {
        setIsOwner(true);
      }
    } catch (error) {
      console.error(error);
      setIsLoading(false);
    }

    try {
      const response = await ChallengeService.getChallengeParticipated();

      // 참여중인 챌린지 배열에서 현재 챌린지 ID와 일치하는 항목 확인
      const participatedChallenge = response.data.find(
        (challenge) => challenge.challengeId === parseInt(id)
      );

      if (participatedChallenge) {
        setIsparticipated(true);

        // 참여 중인 챌린지의 시작 날짜가 현재 날짜보다 이후인지 확인
        const startDate = new Date(participatedChallenge.startDate);
        const today = new Date();
        setIsNotStarted(startDate > today);
      }
      setIsLoading(false);
    } catch (error) {
      console.log(error);
      setIsLoading(false);
    }
  };
  // 페이지가 실행될 때 현재 보고자 하는 챌린지의 상세 내용을 가져온다.
  useEffect(() => {
    fetchData();
  }, []);

  return (
    <>
      <CreateButton />
      <CustomBackHeader title="챌린지" />
      <div className="flex flex-col items-start pb-[82px] gap-3.5 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        {isLoading ? (
          <></>
        ) : (
          <>
            {/* 챌린지 상세 이미지 */}
            <ChallengeDetailImg
              imageInfo={currChallenge.challenge.thumbnailUrl}
            />
            <ChallengeIntro
              challengeType={currChallenge.challenge.challengeType}
              challengeName={currChallenge.challenge.challengeName}
              currentParticipants={currChallenge.participantCount}
              startDate={currChallenge.challenge.startDate}
              endDate={currChallenge.challenge.endDate}
            />

            {/* 챌린지 상세 설명 */}
            <div className="flex flex-col items-center p-[22px_11px_22px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
              <div className="whitespace-pre-line text-left w-full h-auto font-normal text-[17px] leading-8 text-black flex-none order-0 flex-grow-0">
                <div className="pl-4 pr-4">
                  {currChallenge.challenge.description}
                </div>
              </div>
            </div>

            {/* 챌린지 주의사항 */}
            <div className="flex flex-col items-center pl-5 pt-8 pb-8 gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
              <div className="whitespace-pre-line text-left w-full h-auto font-normal text-[17px] leading-[20px] text-black flex-none order-0 flex-grow-0">
                <h2 className="text-lg font-bold mb-1">주의사항</h2>

                <p>
                  {`
                  챌린지 시작 전까지 100% 환불

                  챌린지 시작 후부터 환불 불가

                  참가비용 최소 ${currChallenge.challenge.limitAmount}원
                  `}
                </p>
              </div>
            </div>

            {/* 현재 챌린지들의 현황*/}
            <MyCurrentStatus
              mySpendingAmount={currChallenge.mySpendingAmount}
              targetAmount={currChallenge.challenge.targetAmount}
            />

            <ParticiStatics
              participantCount={currChallenge.participantCount}
              averageSuccessRate={currChallenge.averageSuccessRate}
              bucket24to0={currChallenge.bucket24to0}
              bucket49to25={currChallenge.bucket49to25}
              bucket84to50={currChallenge.bucket84to50}
              bucket100to85={currChallenge.bucket100to85}
              bucketOver100={currChallenge.bucketOver100}
            />
            {/* 참가 버튼 */}
            {!isExpired && !isParticipated && (
              <div className="w-full justify-center flex flex-row">
                <button className="longButton text-white" onClick={handleClick}>
                  참여하기
                </button>
              </div>
            )}

            {/* 참여 취소/챌린지 삭제 버튼 */}
            {isParticipated && isNotStarted && (
              <div className="w-full justify-center flex flex-row">
                <button
                  className="longButton text-white"
                  onClick={isOwner ? handleDelete : handleCancel}
                >
                  {isOwner ? "챌린지 삭제" : "참여 취소"}
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </>
  );
}
