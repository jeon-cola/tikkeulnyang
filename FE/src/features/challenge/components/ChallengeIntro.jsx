/* 사용예시 
<ChallengeIntro
    challengeType="공식챌린지"
    challengeName="택시요금 줄이기"
    currentParticipants="100"
    startDate="03 . 10"
    endDate="03 . 13"
/>
*/

export default function ChallengeIntro({
  challengeType,
  challengeName,
  currentParticipants,
  startDate,
  endDate,
}) {
  // 기간 표시 컴포넌트
  const DateRange = () => {
    return (
      <div className="absolute flex flex-row justify-center items-center p-[2px] px-[7px] gap-[10px] right-5 bottom-4 rounded-[6px]">
        <div className="w-full font-normal text-sm leading-[13px] text-gray-500 ">
          {/*03 . 10 ~ 03 . 15*/}
          {startDate} ~ {endDate}
        </div>
      </div>
    );
  };

  return (
    <>
      <div className="flex flex-col items-center p-[20px_11px_15px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
        <div className="w-full h-[84px] flex-none order-0 flex-grow-0">
          <img
            src="/foot_print.png"
            alt="발자국 아이콘"
            className="absolute w-[17.5px] h-[14.1px] left-[12px] top-[18px] object-contain"
          />
          <div className="absolute w-[61.78px] h-[16px] left-[32.59px] top-[17px] font-normal text-[12px] leading-[16px] text-black">
            {challengeType}
          </div>

          {/* 챌린지 제목 */}
          <div className="text-left absolute w-[337px] h-[32px] top-[42px] font-semibold text-[23px] leading-[27px] text-black">
            {challengeName}
          </div>

          {/* 참가자 정보 그룹 */}
          <div className="absolute w-[77px] h-[17px] left-[18.5px] top-[83px]">
            <img
              src="/human.png"
              alt="사람 아이콘"
              className="absolute w-[17.5px] h-[17px] object-contain"
            />
            <div className="whitespace-nowrap absolute left-[18px] font-normal text-sm leading-[14px] text-black">
              {currentParticipants}명 참가
            </div>
          </div>

          {/* 기간 표시 */}
          <DateRange />
        </div>
      </div>
    </>
  );
}
