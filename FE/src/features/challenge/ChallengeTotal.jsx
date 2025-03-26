import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import CustomHeader from "@/components/CustomHeader";
import ChallengeNav from "@/features/challenge/components/ChallengeNav";
import { ChallengeService } from "@/features/challenge/servies/ChallengeService";
import BasicContainer from "@/features/challenge/components/BasicContainer";
import ChallengeCard from "@/features/challenge/components/ChallengeCard";
import CardBox from "@/features/challenge/components/CardBox";
import ChallengeDesc from "@/features/challenge/components/ChallengeDesc";

export default function ChallengeTotal() {
  const { type } = useParams();
  const [challengeList, setChallengeList] = useState([]);
  const [challengeType, setChallengeType] = useState("");

  useEffect(() => {
    fetchChallengeList();
  }, []);
  // type별로 다르게 axios로 조회
  const fetchChallengeList = async () => {
    let response = null;
    try {
      if (type == "official") {
        response = await ChallengeService.getOfficial(1, 10);
        setChallengeType("공식챌린지");
      } else if (type == "user") {
        response = await ChallengeService.getUser(1, 10);
        setChallengeType("유저챌린지");
      } else if (type == "recommend") {
        response = await ChallengeService.getUser(1, 10); // 추후에 recommend로 바꿀것
        setChallengeType("추천챌린지");
      }
      console.log(response.data.content);
      setChallengeList(response.data.content);
    } catch (error) {
      console.log(error);
    }
  };

  // 전체 챌린지를 격자형태로 렌더링
  const renderChallenge = () => {
    const cardElements = [];

    for (let i = 0; i < challengeList.length; i += 2) {
      cardElements.push(
        <CardBox>
          {challengeList.slice(i, i + 2).map((challenge) => (
            <ChallengeCard
              imageUrl={challenge.imageUrl}
              type={challengeType}
              title={challenge.challengeName}
              startDate={challenge.startDate}
              endDate={challenge.endDate}
              challengeId={challenge.challengeId}
            />
          ))}
        </CardBox>
      );
    }

    console.log("cardElements.length : " + cardElements.length);
    return cardElements;
  };

  // 전체 페이지 렌더링
  return (
    <>
      <CustomHeader title="챌린지" showCreateButton="true" />
      <div className="flex flex-col items-start p-[30px_10px_12px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <BasicContainer>
          <ChallengeDesc type={challengeType} button="" />
          {renderChallenge()}
        </BasicContainer>
      </div>
    </>
  );
}
