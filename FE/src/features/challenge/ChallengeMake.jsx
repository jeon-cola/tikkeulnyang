import CustomHeader from "@/components/CustomHeader";
import NavBar from "@/components/NavBar";
import { useNavigate } from "react-router-dom";
import CustomCalendar from "@/components/CustomCalendar";

export default function ChallengeMake() {
  const navigate = useNavigate();
  const handleClick = () => {
    navigate(`/challenge`); // 챌린지 메인 페이지로 이동
  };

  return (
    <>
      <CustomHeader title="챌린지 생성" />
      <div className="flex flex-col items-start p-[30px_10px_12px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px] h-[70px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                {/* 챌린지 제목 */}
                <h2 className="text-left w-[154.03px] h-[32px] left-0 top-0 font-pretendard font-semibold text-[15px] leading-[18px] text-black">
                  챌린지 제목
                </h2>
              </div>
            </div>

            {/* 입력창 */}
            <div className="box-border w-full h-[38px] bg-white border border-[#DFDFDF] shadow-[0px_0.5px_2px_rgba(0,0,0,0.25)] rounded-[6px] order-1 flex-none">
              <input
                type="email"
                placeholder="챌린지 제목을 입력해주세요"
                className="w-full h-full px-[5px] pt-[5px] font-pretendard font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px] h-auto">
            <div className="w-full h-auto order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                {/* 챌린지 소개 */}
                <h2 className="text-left w-[154.03px] h-[32px] left-0 top-0 font-pretendard font-semibold text-[15px] leading-[18px] text-black">
                  챌린지 소개
                </h2>
              </div>
            </div>

            {/* 입력창 */}
            <div className="box-border w-full h-[168px] bg-white border border-[#DFDFDF] shadow-[0px_0.5px_2px_rgba(0,0,0,0.25)] rounded-[6px] order-1 flex-none">
              <textarea
                placeholder="소개글을 입력해주세요"
                className="w-full h-[168px] resize-none px-[5px] pt-[5px] font-pretendard font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px] h-[70px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                {/* 예치금 최소 금액 설정 */}
                <h2 className="text-left w-[154.03px] h-[32px] left-0 top-0 font-pretendard font-semibold text-[15px] leading-[18px] text-black">
                  예치금 최소 금액 설정
                </h2>
              </div>
            </div>

            {/* 입력창 */}
            <div className="box-border w-full h-[38px] bg-white border border-[#DFDFDF] shadow-[0px_0.5px_2px_rgba(0,0,0,0.25)] rounded-[6px] order-1 flex-none">
              <input
                type="text"
                placeholder="최소 1,000원부터 입력"
                className="w-full h-full px-[5px] pt-[5px] font-pretendard font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <CustomCalendar />
        </div>

        {/* 결제 버튼 */}
        <div className="w-full justify-center flex flex-row">
          <button className="longButton" onClick={handleClick}>
            챌린지 생성
          </button>
        </div>
      </div>

      <NavBar />
    </>
  );
}
