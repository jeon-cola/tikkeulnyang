export default function Homewidget() {
  return (
    <>
      <div className="flex flex-col items-start p-[18px] gap-[20px] w-[176px] h-[177px] bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px]">
        {/* 위젯 내용 */}

        {/* 남은 예산 - 타이틀 */}
        <h3 className="font-['Pretendard'] font-normal text-[20px] leading-[30px] text-black">
          남은 예산
        </h3>

        {/* 196,239원 - 금액 */}
        <p className="font-['Pretendard'] font-normal text-[24px] leading-[36px] text-black mt-[1px]">
          196,239원
        </p>
      </div>
    </>
  );
}
