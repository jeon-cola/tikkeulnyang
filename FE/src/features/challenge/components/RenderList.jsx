import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import { useEffect, useState } from "react";
import ChallengeCard2 from "@/features/challenge/components/ChallengeCard2";

export default function RenderList({ pageType }) {
  const [challengeHistory, setChallengeHistory] = useState([]);
  const [challengeParticipated, setChallengeParticipated] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

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
    setIsLoading(true)
    try {
      const response = await ChallengeService.getChallengeParticipated();
      console.log("참여중인 챌린지:", response.data);
      setChallengeParticipated(response.data);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false)
    }
  };

  useEffect(() => {
    fetchHistory();
    fetchParticipated();
  }, []);

  return (
    <>
      {isLoading ? (
        <>
        {[1,2,3,4].map((_,index)=> (
          <div
          key={index}
            className="flex flex-col items-start px-3 py-5 pl-4 gap-2.5 w-[calc(100%-1.5rem)] rounded-lg shadow-md max-w-sm ml-3 mb-1.5 mt-1.5 bg-white"
          >
            <div className="relative w-full h-[74px]">
              <div className="relative">
                <div className="absolute w-[95px] h-[78px] left-0 top-0 rounded-md bg-cover bg-center"></div>
                  <div className="absolute w-[95px] h-[78px] left-0 top-0 rounded-md bg-gray-400 opacity-40"></div>
              </div>


              <div className="absolute left-[104px] top-3 bg-gray-400 opacity-40 w-3/5 h-3/10 rounded-md"></div>

              <div className="absolute left-[104px] top-11 bg-gray-400 opacity-40 w-1/5 h-3/10 rounded-md"></div>


              <div className="absolute right-4 bottom-0 bg-gray-400 opacity-40 w-1/5 h-3/10 rounded-md"></div>
            </div>
          </div>
        ))}
        </>
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
