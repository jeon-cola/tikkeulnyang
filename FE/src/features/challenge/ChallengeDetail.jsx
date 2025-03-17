import React from "react";
import { useParams } from "react-router-dom";
import CustomHeader from "@/components/CustomHeader";
import ChallengeDetailImg from "@/features/challenge/components/ChallengeDetailImg";
import ChallengeIntro from "@/features/challenge/components/ChallengeIntro";

export default function ChallengeDetail() {
  const { ChallengeId } = useParams();

  return (
    <>
      <CustomHeader />
      <div className="flex flex-col items-start p-[30px_20px_12px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        {/* 챌린지 상세 이미지 */}
        <ChallengeDetailImg imageInfo="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg=" />
        <ChallengeIntro
          challengeType="공식챌린지"
          challengeName="택시요금 줄이기"
          currentParticipants="100"
          startDate="03 . 10"
          endDate="03 . 13"
        />
      </div>
    </>
  );
}
