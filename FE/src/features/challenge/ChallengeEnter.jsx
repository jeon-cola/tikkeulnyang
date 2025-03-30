import CustomHeader from "@/components/CustomHeader";
import NavBar from "@/components/NavBar";
import ChallengeIntro from "@/features/challenge/components/ChallengeIntro";
import { useNavigate, useParams } from "react-router-dom";
import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import { useEffect, useState } from "react";
import { ChallengeUtils } from "@/features/challenge/utils/ChallengeUtils";

export default function ChallengeEnter() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [isLoading, setIsLoading] = useState(true);
  const [challengeData, setChallengeData] = useState({
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
    bucket100to85: 0,
    bucket84to50: 0,
    bucket49to25: 0,
    bucket24to0: 0,
    averageSuccessRate: 0.0,
  });

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
        },
      };

      console.log("formatted response", formattedData);

      setChallengeData(formattedData);
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

  const handleClick = async () => {
    const response = await ChallengeService.postChallengeJoin(id);
    console.log("ChallengeJoin result : ", response);
    navigate(`/challenge/%{id}`); // 추후에 결제 완료 알림창 뜨도록 하게 할것
  };

  return (
    <>
      {isLoading ? (
        <></>
      ) : (
        <>
          <CustomHeader title="챌린지 입장" showCreateButton="true" />
          <div className="flex flex-col items-start p-[30px_20px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
            <ChallengeIntro
              challengeType={challengeData.challenge.challengeType}
              challengeName={challengeData.challenge.challengeName}
              currentParticipants={challengeData.participantCount}
              startDate={challengeData.challenge.startDate}
              endDate={challengeData.challenge.endDate}
            />

            <div className="text-left flex flex-col items-center p-[30px_12px_30px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
              {/* 예치금 제목 */}
              <h2 className="text-2xl font-semibold text-black mb-[8px]">
                예치금
              </h2>

              {/* 설명 텍스트 */}
              <p className="text-base text-black mb-4 leading-tight">
                챌린지 시작 전에 돈을 걸고, 완주를 하지 못했을시에는 예치금을
                돌려받지 못합니다.
                <br />
                <br />
                하지만 성공적으로 완주를 하였을 시에는, 완주를 하지 못한 다른
                참가자들의 돈까지 상금으로 드립니다!
              </p>

              {/* 금액 표시 및 밑줄 */}
              <div className="w-full flex flex-col items-center">
                <span className="text-2xl font-semibold text-[#FF957A] mb-1">
                  {challengeData.challenge.limitAmount}원
                </span>
                <div className="w-1/2 max-w-xs h-px bg-[#FF957A]"></div>
              </div>
            </div>
            <div className="flex flex-col items-center p-[30px_12px_30px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
              <div className="w-[348px] h-[90px] flex-none order-0 self-stretch flex-grow-0 relative">
                {/* 제목 부분 */}

                <h2 className=" w-[194.95px] h-[32px] left-[12.05px] top-[3px] font-[Pretendard] font-bold text-[23px] leading-[27px] text-black">
                  예치금 충전 및 결제
                </h2>

                {/* 예치금 정보 그룹 */}
                <div className="relative flex flex-col w-full h-[46px]">
                  {/* 참가 예치금 행 */}
                  <div className=" w-[348px] h-[20px] left-[12px] top-[1px]">
                    <span className="absolute w-[78.73px] h-[20px] left-[12px] top-[5px] font-[Pretendard] font-normal text-[17px] leading-[20px] text-black">
                      참가 예치금
                    </span>
                    <span className="absolute w-[62px] h-[20px] left-[298px] top-[5px] font-[Pretendard] font-normal text-[17px] leading-[20px] text-black">
                      1,000원
                    </span>
                  </div>

                  {/* 현재 보유 예치금 행 */}
                  <div className="w-[348px] h-[20px] left-[12px] top-[100px]">
                    <span className="absolute w-[115.24px] h-[20px] left-[12px] top-[45px] font-[Pretendard] font-normal text-[17px] leading-[20px] text-black">
                      현재 보유 예치금
                    </span>
                    <span className="absolute w-[70px] h-[20px] left-[290px] top-[45px] font-[Pretendard] font-normal text-[17px] leading-[20px] text-black">
                      10,000원
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* 결제 버튼 */}
            <div className="w-full justify-center flex flex-row">
              <button className="longButton" onClick={handleClick}>
                결제하기
              </button>
            </div>
          </div>
          <NavBar />
        </>
      )}
    </>
  );
}
