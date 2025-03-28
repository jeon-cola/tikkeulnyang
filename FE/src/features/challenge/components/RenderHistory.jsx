import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import { useEffect, useState } from "react";
import ChallengeCard2 from "@/features/challenge/components/ChallengeCard2";

export default function RenderHistory() {
  const [challengeHistory, setChallengeHistory] = useState([]);
  const fetchHistory = async () => {
    try {
      const response = await ChallengeService.getPast();
      console.log(response);
      setChallengeHistory(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  return (
    <>
      {challengeHistory.map((challenge) => (
        <ChallengeCard2
          thumbnailUrl={challenge.thumbnailUrl}
          challengeName={challenge.challengeName}
          challengeType={challenge.challengeType}
          targetAmount={challenge.targetAmount}
          startDate={challenge.startDate}
          endDate={challenge.endDate}
          challengeId={challenge.challengeId}
        />
      ))}
    </>
  );
}
