import React from "react";
/* 사용 예시
  <ChallengeCard
    imageUrl="https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg="
    type="공식챌린지"
    title="카페 방문 줄이기"
    startDate="01-01"
    endDate="01-31"
  />
*/

export default function challengeCard({
  imageUrl,
  type,
  title,
  startDate,
  endDate,
}) {
  return (
    <div className="absolute w-[168px] h-[207px] left-[15px] top-[82px] bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] overflow-hidden">
      {/* 이미지 영역 */}
      <div
        className="absolute w-[168px] h-[129px] left-0 top-0"
        style={{ backgroundImage: `url(${imageUrl})`, backgroundSize: "cover" }}
      ></div>

      {/* 챌린지 타입*/}
      <div className="absolute w-[48px] h-[13px] left-[7px] top-[134px] font-['Pretendard'] font-normal text-[11px] leading-[13px] text-black">
        {type}
      </div>

      {/* 카페 방문 줄이기 제목 */}
      <div className="absolute w-[86px] h-[16px] left-[7px] top-[156px] font-['Pretendard'] font-normal text-[13px] leading-[16px] text-black">
        {title}
      </div>

      {/* 날짜 표시*/}
      <div className="absolute w-[67px] h-[11px] left-[94px] top-[188px] bg-[#DFDFDF] shadow-[0px_1px_1.5px_rgba(0,0,0,0.25)] rounded-[6px] flex items-center justify-center">
        <span className="font-['Pretendard'] font-normal text-[7px] leading-[8px] text-black">
          {startDate} ~ {endDate}
        </span>
      </div>
    </div>
  );
}
