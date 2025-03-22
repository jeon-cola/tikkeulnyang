import React from "react";
/*
    <ViewMoreButton onClick={() => {}} />
*/
export default function ViewMoreButton({ onClick }) {
  return (
    <div className="flex flex-col items-start px-[78px] pr-[79px] gap-2.5 w-[370px] h-7 bg-white flex-none order-3 self-stretch flex-grow-0">
      <div className="box-border flex flex-row justify-center items-center px-4 py-1.5 gap-2.5 w-[209px] h-7 bg-white border border-[#DFDFDF] rounded-full flex-none order-0 flex-grow-0">
        <span
          className="w-[97px] h-4 font-['Pretendard'] font-normal text-[13px] leading-4 text-[#757D86] flex-none order-0 flex-grow-0"
          onClick={onClick}
        >
          더보기
        </span>
      </div>
    </div>
  );
}
