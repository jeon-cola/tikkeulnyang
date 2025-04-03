{
  /* 사용예시
    <MyCurrentStatus deposit="10000" currentProgress="50" /> 
*/
}

export default function MyCurrentStatus({
  deposit = 5000,
  currentProgress = 50,
}) {
  return (
    <>
      {/* 나의 현황 */}
      <div className="flex flex-col items-center p-[25px_11px_25px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
        <div className="w-full h-[109px] relative">
          {/* 타이틀 */}
          <div className="absolute top-[6px]">
            <h2 className="text-black text-[15px] font-semibold leading-[18px] font-['Pretendard']">
              나의 현황
            </h2>
          </div>

          {/* 달성률 그룹 */}
          <div className="absolute left-[15px] top-[38px] flex flex-col">
            <span className="text-black text-[15px] font-normal leading-[23px] font-['Prompt']">
              달성률
            </span>
            <span className="text-black text-[15px] font-normal leading-[23px] font-['Prompt']">
              {currentProgress}%
            </span>
          </div>

          {/* 예치금 그룹 */}
          <div className="text-black absolute right-[15px] top-[38px] flex flex-col items-end">
            <span className="text-[15px] font-normal leading-[23px] font-['Prompt']">
              예치금
            </span>
            <span className="text-[15px] font-normal leading-[23px] font-['Prompt']">
              {deposit}원
            </span>
          </div>

          {/* 프로그레스 바 배경 */}
          <div className="absolute left-0 bottom-0 w-full h-[24px] bg-[#F1EFEF] border border-[#DFDFDF] rounded-[70px]">
            {/* 프로그레스 바 채움 */}
            <div
              className="h-full bg-[#FF957A] rounded-[70px]"
              style={{ width: `${currentProgress}%` }}
            ></div>
          </div>
        </div>
      </div>
    </>
  );
}
