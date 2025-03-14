import React from "react";

export default function ChallengeNav() {
  return (
    <>
      <div className="relative w-[372px] h-[42px] bg-white rounded-full flex-none order-0 self-stretch flex-grow-0">
        <div className="flex flex-row justify-center items-center p-[6px_16px] gap-[10px] absolute w-[125px] h-[43px] left-0 top-0 bg-[#FF957A] border border-[#DFDFDF] rounded-full">
          <span className="w-auto h-[20px] font-normal text-[17px] leading-[20px] text-white flex-none order-0 flex-grow-0">
            전체 챌린지
          </span>
        </div>

        <div className="box-border flex flex-row justify-center items-center p-[6px_16px] gap-[10px] absolute w-[125px] h-[42px] left-[127px] top-0 bg-white rounded-full">
          <span className="w-auto h-[20px] font-normal text-[17px] leading-[20px] text-[#303030] flex-none order-0 flex-grow-0">
            참여중 챌린지
          </span>
        </div>

        <div className="flex flex-row justify-center items-center p-[6px_16px] gap-[10px] absolute w-[125px] h-[42px] left-[254px] top-0 bg-white rounded-full">
          <span className="w-auto h-[20px] font-normal text-[17px] leading-[20px] text-[#303030] flex-none order-0 flex-grow-0">
            참여이력
          </span>
        </div>
      </div>
    </>
  );
}
