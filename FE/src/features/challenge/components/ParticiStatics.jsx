import RoundChart from "@/features/challenge/components/RoundChart";
/**
 *참가자들의 현황 보여주는 컴포넌트
 추후 axios로 수정해주어야 할 부분 : 총 참가자수, 평균 예상 달성률, 각 구간별 예상 달성자수
 */
export default function ParticiStatics({
  participantCount = 100,
  averageSuccessRate = 50,
  bucket24to0 = 50,
  bucket49to25 = 50,
  bucket84to50 = 50,
  bucket100to85 = 50,
  bucketOver100 = 50,
}) {
  return (
    <>
      <div className="flex flex-col items-center p-[30px_12px_30px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
        {/* 서두 */}

        {/* 제목 */}
        <div className=" w-[318px] h-[32px] left-[15px] top-[6px]">
          <h2 className=" w-[318px] h-[32px] left-0 top-0 font-['Pretendard'] font-semibold text-[15px] leading-[18px] text-black">
            참가자 현황
          </h2>
        </div>
        <div className="relative">
          {/* 참가자 정보 */}
          <div className=" w-[324px] h-[44px] left-[15px] top-[38px]">
            {/*  참가자 수 */}
            <div className=" w-[75px] h-[44px] left-0 top-0">
              <p className="whitespace-nowrap absolute w-[75px] h-[23px] left-0 top-0 font-['Prompt'] font-normal text-[15px] leading-[23px] text-black">
                총 참가자수
              </p>
              <p className="absolute w-[56.74px] h-[23px] left-0 top-[21px] font-['Prompt'] font-normal text-[15px] leading-[23px] text-black">
                {participantCount}명
              </p>
            </div>

            {/* 달성률 영역 */}
            <div className="absolute flex flex-rows  w-[108px] h-[44px] left-[231px] top-0">
              <p className="whitespace-nowrap absolute w-[108px] h-[23px] right-[12px] top-0 font-['Prompt'] font-normal text-[15px] leading-[23px] text-black">
                평균 소비 비율
              </p>
              <p className="absolute w-[30px] h-[23px] right-[14px] top-[21px] font-['Prompt'] font-normal text-[15px] leading-[23px] text-black">
                {averageSuccessRate}%
              </p>
            </div>
          </div>
        </div>

        {/* 원형 차트 */}
        <RoundChart
          data={[averageSuccessRate, 100 - averageSuccessRate]}
        ></RoundChart>
        {/* 참가자 현황 상세 */}
        <div className="relative w-[293px] pt-2 pb-2 h-44 bg-white shadow-sm rounded-md">
          <div className="absolute gap-4 flex flex-col w-[248px] h-auto left-[23px] top-[13px]">
            {/* 100 ~%  항목 */}
            <div className=" w-full h-[17px] flex items-center">
              {/* 발바닥 아이콘 */}
              <img
                src="/foot_print.png"
                alt="아이콘"
                className="w-[17px] h-[14px]"
              />

              {/* 등급 범위 텍스트 */}
              <div className="ml-[26px] w-[86px] font-['Pretendard'] text-[13px] leading-4 text-black">
                ~ 100%
              </div>

              {/* 인원 수 */}
              <div className="ml-auto font-['Pretendard'] text-[13px] leading-4 text-black">
                {/*studentCounts[0]*/ `${bucketOver100}명`}
              </div>
            </div>

            {/* 100 ~ 86% 항목 */}
            <div className="flex w-full h-[17px] left-0 top-0 items-center">
              {/* 발바닥 아이콘 */}
              <img
                src="/foot_print.png"
                alt="아이콘콘"
                className="w-[17px] h-[14px]"
              />

              {/* 등급 범위 텍스트 */}
              <div className="ml-[26px] w-[86px] font-['Pretendard'] text-[13px] leading-4 text-black">
                100 ~ 86%
              </div>

              {/* 인원 수 */}
              <div className="ml-auto font-['Pretendard'] text-[13px] leading-4 text-black">
                {/*studentCounts[0]*/ `${bucket100to85}명`}
              </div>
            </div>

            {/* 85 ~ 51% 항목 */}
            <div className="w-full h-[17px] flex items-center">
              {/* 발바닥 아이콘 */}
              <img
                src="/foot_print.png"
                alt="발바닥"
                className="w-[17px] h-[14px]"
              />

              {/* 등급 범위 텍스트 */}
              <div className="ml-[26px] w-[86px] font-['Pretendard'] text-[13px] leading-4 text-black">
                85 ~ 51%
              </div>

              {/* 인원 수 */}
              <div className="ml-auto font-['Pretendard'] text-[13px] leading-4 text-black">
                {/*studentCounts[1]*/ `${bucket84to50}명`}
              </div>
            </div>

            {/* 50 ~ 26% 항목 */}
            <div className="w-full h-[17px] flex items-center">
              {/* 발바닥 아이콘 */}
              <img
                src="/foot_print.png"
                alt="발바닥"
                className="w-[17px] h-[14px]"
              />

              {/* 등급 범위 텍스트 */}
              <div className="ml-[26px] w-[86px] font-['Pretendard'] text-[13px] leading-4 text-black">
                50 ~ 26%
              </div>

              {/* 인원 수 */}
              <div className="ml-auto font-['Pretendard'] text-[13px] leading-4 text-black">
                {/*studentCounts[2]*/ `${bucket49to25}명`}
              </div>
            </div>

            {/* 25 ~ 0% 항목 */}
            <div className="w-full h-[17px] flex items-center">
              {/* 발바닥 아이콘 */}
              <img
                src="/foot_print.png"
                alt="발바닥"
                className="w-[17px] h-[14px]"
              />

              {/* 등급 범위 텍스트 */}
              <div className="ml-[26px] w-[86px] font-['Pretendard'] text-[13px] leading-4 text-black">
                25 ~ 0%
              </div>

              {/* 인원 수 */}
              <div className="ml-auto font-['Pretendard'] text-[13px] leading-4 text-black">
                {/*studentCounts[3]*/ `${bucket24to0}명`}
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
