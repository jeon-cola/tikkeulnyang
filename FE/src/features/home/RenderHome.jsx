import CustomHeader from "@/components/CustomHeader";
import HomeWidget from "@/features/home/components/HomeWidget";

export default function RenderHome() {
  return (
    <>
      <CustomHeader title="홈" />
      <div className="flex flex-col items-start p-[30px_10px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <div className="flex flex-col p-[12px_11px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]">
          <p className="w-auto h-[12px] font-['Pretendard'] text-left font-semibold text-[10px] leading-[12px] tracking-[0.07em] text-[#D0D0D0]">
            이번달 카드 추천
          </p>

          <p className="w-auto h-[14px] font-['Pretendard'] text-left font-semibold text-[12px] leading-[14px] tracking-[0.07em] text-black">
            혜택이 많은 카드를 골라
          </p>

          <p className="w-auto h-[13px] font-['Pretendard'] text-left font-semibold text-[11px] leading-[13px] tracking-[0.07em] text-black">
            카드 혜택으로 절약해보아요
          </p>

          <div className="absolute flex flex-row justify-center items-center p-[3px_5px] gap-[10px] w-[37px] h-[12px] right-[16px] bottom-[5px] bg-[rgba(8,8,8,0.46)] rounded-[30px]">
            <span className="w-[25px] h-[6px] font-['Pretendard'] font-semibold text-[5px] leading-[6px] tracking-[0.07em] text-white">
              자세히 보기
            </span>
          </div>
        </div>

        <div className="flex flex-col p-[12px_20px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]">
          <h2 className="pt-[18px] font-['Pretendard'] font-semibold text-[14px] text-left leading-[17px] tracking-[0.01em] text-[#FF957A] mb-[6px]">
            안녕하세요 유저님
          </h2>

          <p className="font-['Pretendard'] font-semibold text-[12px] text-left leading-[22px] tracking-[0.01em] text-black">
            주 5회 무지출 챌린지에 도전 중이시군요
            <br />
            곧 마감일이 다가오고 있어요
            <br />
            끝까지 힘내요
          </p>

          <div className="pt-[99px] w-full justify-center flex flex-row">
            <button className=" text-white longButton">
              챌린지 자세히보기
            </button>
          </div>
        </div>

        <div className="flex flex-col p-[12px_20px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start mt-[20px]">
            {/* 출석체크하고 경험치 받기 - 제목 텍스트 */}
            <h2 className=" font-['Pretendard'] text-left font-semibold text-[14px] leading-[17px] tracking-[0.07em] text-black mb-[7px]">
              가계부로 이동
            </h2>

            {/* 귀여운 발도장 받고 캐릭터 레벨업 해요 - 설명 텍스트 */}
            <p className="mb-[24px] text-left font-['Pretendard'] font-semibold text-[12px] leading-[14px] tracking-[0.07em] text-black">
              오늘의 지출 확인하기
            </p>
          </div>
        </div>

        <div className="flex justify-between w-full h-auto mt-[1px]">
          {/* 위젯으로 모아보는 티끌냥 - 헤더 제목 */}
          <h2 className="ml-[20px] font-['Pretendard'] font-semibold text-[14px] leading-[17px] tracking-[0.01em] text-black">
            위젯으로 모아보는 티끌냥
          </h2>

          {/* 개인 버튼 */}
          <div className="flex justify-center items-center px-[5px] py-[3px] w-[52px] h-[23px] bg-white shadow-[1px_1px_4px_rgba(0,0,0,0.1)] rounded-[30px]">
            <span className="font-['Pretendard'] font-normal text-[12px] leading-[14px] tracking-[0.07em] text-[#303030]">
              편집
            </span>
          </div>
        </div>
        <div className="flex flex-row justify-between w-full">
          <HomeWidget />
          <HomeWidget />
        </div>

        <div className="flex flex-row justify-between w-full pt-[34px]">
          <HomeWidget />
          <HomeWidget />
        </div>

        <div className="flex flex-row justify-between w-full pt-[34px]">
          <HomeWidget />
          <HomeWidget />
        </div>
      </div>
    </>
  );
}
