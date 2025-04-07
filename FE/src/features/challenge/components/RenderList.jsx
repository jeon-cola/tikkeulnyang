import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import { useEffect, useState } from "react";
import ChallengeCard2 from "@/features/challenge/components/ChallengeCard2";

export default function RenderList({ pageType }) {
  const [challengeHistory, setChallengeHistory] = useState([]);
  const [challengeParticipated, setChallengeParticipated] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // 참여 이력 조회
  const fetchHistory = async () => {
    try {
      const response = await ChallengeService.getPast();
      console.log(response);
      setChallengeHistory(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  // 참여중인 챌린지 조회
  const fetchParticipated = async () => {
    try {
      const response = await ChallengeService.getChallengeParticipated();
      console.log("참여중인 챌린지:", response.data);
      setChallengeParticipated(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    fetchHistory();
    fetchParticipated();
    console.log("참여중인 챌린지:", challengeParticipated);
    setIsLoading(false);
  }, []);

  return (
    <>
      {isLoading ? (
        <div>Loading...</div>
      ) : (
        <>
          {pageType === "past"
            ? challengeHistory.map((challenge) => (
                <ChallengeCard2
                  thumbnailUrl={challenge.thumbnailUrl}
                  challengeName={challenge.challengeName}
                  challengeType={challenge.challengeType}
                  targetAmount={challenge.targetAmount}
                  startDate={challenge.startDate}
                  endDate={challenge.endDate}
                  challengeId={challenge.challengeId}
                  participationStatus={challenge.participationStatus}
                  pageType="past"
                />
              ))
            : challengeParticipated.map((challenge) => (
                <ChallengeCard2
                  thumbnailUrl={challenge.thumbnailUrl}
                  challengeName={challenge.challengeName}
                  challengeType={challenge.challengeType}
                  targetAmount={challenge.targetAmount}
                  startDate={challenge.startDate}
                  endDate={challenge.endDate}
                  challengeId={challenge.challengeId}
                  participationStatus={challenge.participationStatus}
                  pageType="participated"
                />
              ))}
        </>
      )}
    </>
  );
}
