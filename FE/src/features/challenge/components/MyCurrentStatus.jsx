{
  /* 사용예시
    <MyCurrentStatus deposit="10000" currentProgress="50" /> 
*/
}

export default function MyCurrentStatus({
  mySpendingAmount = 3300,
  targetAmount = 5000,
}) {
  return (
    <>
      {/* 나의 현황 */}
      <div className="flex flex-col items-center pl-5 p-[25px_11px_25px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
        <div className="w-full h-[109px] relative">
          {/* 타이틀 */}
          <div className="absolute top-[6px]">
            <h2 className="text-black text-[15px] font-semibold leading-[18px] font-['Pretendard']">
              나의 현황
            </h2>
          </div>

          {/* 달성률 그룹 */}
          <div className="relative flex flex-row justify-between">
            <div className="absolute left-[9px] top-[38px] text-left flex flex-col">
              <span className="text-black text-[15px] font-normal leading-[23px] font-['Prompt']">
                나의 소비
              </span>
              <span className="text-black text-[15px] font-normal leading-[23px] font-['Prompt']">
                {mySpendingAmount}원
              </span>
            </div>

            {/* 예치금 그룹 */}
            <div className="text-black text-right absolute right-[15px] top-[38px] flex flex-col items-end">
              <span className="text-[15px] font-normal leading-[23px] font-['Prompt']">
                한계금액
              </span>
              <span className="text-[15px] font-normal leading-[23px] font-['Prompt']">
                {targetAmount}원
              </span>
            </div>
          </div>

          {/* 프로그레스 바 배경 */}
          <div className="absolute left-0 bottom-0 w-full h-[24px] bg-[#F1EFEF] border border-[#DFDFDF] rounded-[70px]">
            {/* 프로그레스 바 채움 */}
            <div
              className="h-full bg-[#FF957A] rounded-[70px]"
              style={{ width: `${mySpendingAmount / targetAmount}%` }}
            ></div>
          </div>
        </div>
      </div>
    </>
  );
}
