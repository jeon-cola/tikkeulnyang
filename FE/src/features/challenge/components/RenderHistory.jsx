import { ChallengeService } from "@/features/challenge/servies/ChallengeService";
import { useEffect, useState } from "react";
import ChallengeCard2 from "@/features/challenge/components/ChallengeCard2";

export default function RenderHistory() {
  const [challengeHistory, setChallengeHistory] = useState([]);
  const fetchHistory = async () => {
    try {
      const response = await ChallengeService.getPast();
      console.log(response.data.content);
      setChallengeHistory(response.data.content);
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
          imageUrl={challenge.imageUrl}
          title={challenge.challengeName}
          category={challenge.challengeType}
          amount={challenge.limitAmount}
          startDate={challenge.startDate}
          endDate={challenge.endDate}
          challengeId={challenge.challengeId}
        />
      ))}
    </>
  );
}
