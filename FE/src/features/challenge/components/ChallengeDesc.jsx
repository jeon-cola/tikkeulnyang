import { useNavigate } from "react-router-dom";

export default function ChallengeDesc({ type, button }) {
  const navigate = useNavigate();
  let path = "";

  const handleClick = () => {
    console.log(type);
    if (type == "추천 챌린지") {
      path = "recommend";
    } else if (type == "공식 챌린지") {
      path = "official";
    } else if (type == "유저 챌린지") {
      path = "user";
    }
    navigate(`/challenge/total/${path}`);
  };

  return (
    <>
      <div className="flex w-[370px]">
        <div className="flex justify-between items-center w-full self-stretch">
          {/* 추천 챌린지 텍스트 */}
          <h2 className="pl-3 font-inter font-semibold text-xl leading-7 text-black">
            {type}
          </h2>

          {/* 전체보기 > 버튼 */}
          <span
            onClick={handleClick}
            className=" pr-[5px] font-inter font-medium text-md leading-[21px] text-black"
          >
            {button}
          </span>
        </div>
      </div>
    </>
  );
}
