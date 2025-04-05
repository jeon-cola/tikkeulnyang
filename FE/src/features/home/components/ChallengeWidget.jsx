import { useNavigate } from "react-router-dom";

export default function ChallengeWidget({
  challengeId = "1",
  thumbnailUrl = "undefined",
  challengeName = "undefined",
}) {
  const navigate = useNavigate();
  return (
    <>
      <div
        className="flex flex-col p-[12px_20px_12px] gap-[5px] relative w-full h-auto bg-white rounded-[6px]"
        style={{
          backgroundImage: `url(${thumbnailUrl})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
          backgroundRepeat: "no-repeat",
        }}
        onClick={() => {
          navigate(`/challenge/${challengeId}`);
        }}
      >
        <div className="absolute inset-0 bg-black/25 backdrop-blur-sm rounded-[6px]"></div>

        <div className="z-10">
          <h2 className="pt-[18px] font-semibold text-xl text-left leading-[17px] tracking-[0.01em] text-white mb-[6px]">
            {challengeName} 챌린지
          </h2>

          <div className="font-light text-lg text-right pt-10 leading-7 tracking-[0.01em] text-white">
            {challengeName} 챌린지
          </div>

          {/* <div className="pt-[99px] w-full justify-center flex flex-row"></div> */}
        </div>
      </div>
    </>
  );
}
