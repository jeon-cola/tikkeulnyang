import { useNavigate } from "react-router-dom";
import { ChallengeUtils } from "@/features/challenge/utils/ChallengeUtils";

{
  /**
    챌린지 메인페이지에서, 
    참여중 챌린지, 참여이력을 탭했을 시에 보여지는 챌린지 카드 컴포넌트
    */
}
export default function ChallengeCard2({
  thumbnailUrl = "https://media.istockphoto.com/id/1552871673/ko/%EC%82%AC%EC%A7%84/%ED%95%98%EC%96%80-%EC%9C%A0%EB%A6%AC%EC%9E%94%EC%97%90-%EB%8B%B4%EA%B8%B4-%EB%B8%94%EB%9E%99-%EC%BB%A4%ED%94%BC%EB%8A%94-%EC%BB%A4%ED%94%BC-%EC%9B%90%EB%91%90%EA%B0%80-%EC%9E%88%EB%8A%94-%EC%98%A4%EB%9E%98%EB%90%9C-%EC%8B%9C%EB%A9%98%ED%8A%B8-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%9C%84%EC%97%90-%EB%86%93%EC%97%AC-%EC%9E%88%EB%8B%A4.jpg?s=612x612&w=0&k=20&c=I7irn9wSVxvSSHVNFSxpxTHFkBcCJlHL0m4NIiTc3Sg=",
  challengeName = "카페 방문 줄이기",
  challengeType = "공식챌린지",
  targetAmount = "10,000원",
  startDate = "01-01",
  endDate = "01-31",
  challengeId = 1,
}) {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/challenge/${challengeId}`);
  };

  return (
    <>
      <div
        onClick={handleClick}
        className="flex flex-col items-start px-3 py-5 pl-4 gap-2.5 w-full max-w-sm ml-3 mb-1.5 mt-1.5 bg-white rounded-3xl shadow-[1px_1px_5px_rgba(0,0,0,0.25)] "
      >
        {/* 내부 컨텐츠 영역 - 상대적 위치 지정으로 레이아웃 구성 */}
        <div className="relative w-full h-[74px]">
          {/* 이미지 영역 - 절대 위치로 배치 */}
          <div
            className="absolute w-[90px] h-[74px] left-0 top-0 rounded-md bg-cover bg-center"
            style={{ backgroundImage: `url(${thumbnailUrl})` }}
          ></div>

          {/* 텍스트 영역 - 제목 (택시 요금 줄이기) */}
          {/* - 텍스트 스타일, 크기, 위치 설정 */}
          <div className="absolute left-[104px] top-4 font-semibold text-base font-['Pretendard'] leading-5 text-black">
            {challengeName}
          </div>

          {/* 카테고리 라벨 (공식챌린지) */}
          <div className="absolute left-[104px] top-11 font-medium text-xs text-gray-600 leading-tight font-['Pretendard']">
            {challengeType}
          </div>

          {/* 하단 정보 (예상 환급액) */}
          {/* <div className="absolute left-[104px] top-14 font-normal text-xs leading-tight font-['Pretendard'] text-black">
            예상 환급액 : {targetAmount}
          </div> */}

          {/* 날짜 프레임 - 우측 하단 배치 */}
          {/* - 그림자, 배경색, 둥근 모서리 적용 */}
          {/* <div className="absolute right-0 bottom-0 w-[69px] h-[15px] bg-gray-200 shadow-sm rounded-md flex items-center justify-center">
            <div className="text-[8px] leading-tight text-black">
              {ChallengeUtils.formatDate(startDate)}~
              {ChallengeUtils.formatDate(endDate)}
            </div>
          </div> */}

          <div className="absolute right-4 bottom-0 w-fill h-[15px] leading-5 text-black flex items-center justify-center">
            <div className=" leading-tight text-black text-xs font-['Pretendard']">
              {ChallengeUtils.formatDate(startDate)} ~
              {ChallengeUtils.formatDate(endDate)}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
