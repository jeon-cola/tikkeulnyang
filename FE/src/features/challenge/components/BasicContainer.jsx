import React from "react";
/* 사용예시
    <BasicContainer>
        <ChallengeNav />
        <ChallengeCard/>
    </BasicContainer>
*/
export default function BasicContainer({ children }) {
  return (
    <>
      <div className="flex flex-col items-center p-[28px_11px_12px] gap-[22px] relative w-[392px] h-[580px] bg-white rounded-[6px]">
        {/* prop으로 전달받은 내용을 나열 */}
        {children}
      </div>
    </>
  );
}
