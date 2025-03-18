import React from "react";
import { useParams } from "react-router-dom";
import CustomHeader from "@/components/CustomHeader";
import ChallengeDetailImg from "@/features/challenge/components/ChallengeDetailImg";
import ChallengeIntro from "@/features/challenge/components/ChallengeIntro";
import MyCurrentStatus from "@/features/challenge/components/MyCurrentStatus";
/*
  추후에 axios로 채워넣을 데이터: 
  title, imageInfo, challengeType, challengeName, currentParticipants, startDate, endDate,
   챌린지 상세 설명,
   deposit, currentProgress, 
*/
export default function ChallengeDetail() {
  const { ChallengeId } = useParams();

  return (
    <>
      <CustomHeader title="챌린지 상세" />
      <div className="flex flex-col items-start p-[30px_20px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        {/* 챌린지 상세 이미지 */}
        <ChallengeDetailImg imageInfo="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg=" />
        <ChallengeIntro
          challengeType="공식챌린지"
          challengeName="택시요금 줄이기"
          currentParticipants="100"
          startDate="03 . 10"
          endDate="03 . 13"
        />

        {/* 챌린지 상세 설명 */}
        <div className="flex flex-col items-center p-[12px_11px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="whitespace-pre-line text-left w-full h-auto font-normal text-[17px] leading-[20px] text-black flex-none order-0 flex-grow-0">
            <p>
              {`
              택시 요금 줄이기 챌린지는 개인 재정 관리의 효과적인 첫걸음입니다.

              이유
              주 2-3회 택시 이용만으로도 월 10-15만원이 지출됩니다. 
              이 비용을 줄이면 저축이나 다른 가치 있는 경험에 투자할 수 있습니다.

              부가효과 
              재정 관리 능력 향상
              걷기나 자전거 이용으로 건강 개선 시간
              관리 능력 발전
              다른 불필요한 지출 패턴도 인식하게 됨

              한 달만 실천해도 눈에 띄는 절약 효과를 경험할 수 있습니다.
              `}
            </p>
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

              참가비용 최소 1000원
              `}
            </p>
          </div>
        </div>

        <MyCurrentStatus deposit="10000" currentProgress="50" />
        {/* 참가 버튼 */}
        <div className="w-full justify-center flex flex-row">
          <button className="longButton">참여하기</button>
        </div>
      </div>
    </>
  );
}
