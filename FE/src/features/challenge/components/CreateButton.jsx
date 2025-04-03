{
  /* 헤더에 보일 챌린지 생성 버튼(+) */
}
import { useNavigate } from "react-router-dom";

export default function CreateButton() {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/challenge/make`);
  };

  return (
    <>
      <div
        onClick={handleClick}
        className="fixed top-[25px] right-[30px] transform scale-y-[-1] z-[51]"
      >
        <div className="absolute w-[21px] h-0 border-black border-t-[2px]"></div>
        <div className="absolute w-[21px] h-0 border-black border-t-[2px] transform rotate-90"></div>
      </div>
    </>
  );
}
