import CustomHeader from "@/components/CustomHeader";
import NavBar from "@/components/NavBar";
import { useNavigate } from "react-router-dom";
import CustomCalendar from "@/components/CustomCalendar";
import { ChallengeService } from "@/features/challenge/servies/ChallengeService";
import { useState, useEffect } from "react";

export default function ChallengeMake() {
  const navigate = useNavigate();

  const [challengeData, setChallengeData] = useState({
    challengeName: "",
    description: "",
    targetAmount: "",
    startDate: "",
    endDate: "",
    limitAmount: "",
    challengeCategory: "",
    imageUrl: "",
    maxParticipants: "",
  });

  // 썸네일 이미지 파일을 저장할 state
  const [thumbnail, setThumbnail] = useState(null);

  useEffect(() => {
    const uploadThumbnail = async () => {
      try {
        if (thumbnail) {
          const response = await ChallengeService.postChallengeThumbnail(
            4,
            thumbnail
          );
          console.log("썸네일 업로드 성공:", response);
        }
      } catch (error) {
        console.error("썸네일 업로드 실패:", error);
      }
    };

    uploadThumbnail();
  }, [thumbnail]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setChallengeData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async () => {
    try {
      await ChallengeService.postChallengeCreate(challengeData);
      //TODO : 추후에 챌린지 알림 페이지도 이동하게끔 수정
      navigate(`/challenge`);
    } catch (error) {
      console.error("챌린지 생성 실패", error);
      alert("챌린지 생성에 실패했습니다. ");
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setThumbnail(file);
      console.log("선택된 파일:", file);
    }
  };

  // 카테고리 항목 개별 컴포넌트
  const CategoryItem = ({ text }) => {
    const handleClick = () => {
      setChallengeData((prev) => ({
        ...prev,
        challengeCategory: text,
      }));
    };
    return (
      <div
        onClick={handleClick}
        className={`box-border flex flex-row justify-center items-center py-[7px] px-[13px] gap-[10px] bg-white border border-[#E1E1E2] rounded-[100px] w-auto h-auto flex-none flex-grow-0`}
      >
        <span className="font-['Noto_Sans_KR'] font-normal text-[14px] leading-[17px] flex items-center tracking-[0.02em] text-[#999999] whitespace-nowrap">
          {text}
        </span>
      </div>
    );
  };

  return (
    <>
      <CustomHeader title="챌린지 생성" />
      <div className="flex flex-col items-start p-[30px_10px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
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
                type="text"
                name="challengeName"
                value={challengeData.challengeName}
                onChange={handleInputChange}
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
                name="description"
                value={challengeData.description}
                onChange={handleInputChange}
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
                type="number"
                name="limitAmount"
                value={challengeData.limitAmount}
                onChange={handleInputChange}
                placeholder="최소 1,000원부터 입력"
                className="w-full h-full px-[5px] pt-[5px] font-pretendard font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px] h-[70px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                {/* 챌린지 카테고리 설정 */}
                <h2 className="text-left w-[154.03px] h-[32px] left-0 top-0 font-pretendard font-semibold text-[15px] leading-[18px] text-black">
                  챌린지 카테고리 설정
                </h2>
              </div>
            </div>

            {/* 챌린지 목록 */}
            <div className="w-full overflow-hidden flex flex-row overflow-x-auto whitespace-nowrap items-center p-0 gap-[10px] relative w-[543px] h-auto">
              <CategoryItem text="주유" />
              <CategoryItem text="세차" />
              <CategoryItem text="이커머스" />
              <CategoryItem text="백화점" />
              <CategoryItem text="마트" />
              <CategoryItem text="버스" />
              <CategoryItem text="지하철" />
              <CategoryItem text="택시" />
              <CategoryItem text="도서" />
              <CategoryItem text="문구" />
              <CategoryItem text="전시/박람회" />
              <CategoryItem text="아동 장난감" />
              <CategoryItem text="통신" />
              <CategoryItem text="OTT 구독" />
              <CategoryItem text="게임 과금" />
              <CategoryItem text="음원" />
              <CategoryItem text="전자기기" />
              <CategoryItem text="직구" />
              <CategoryItem text="면세점" />
              <CategoryItem text="여행" />
              <CategoryItem text="카페" />
              <CategoryItem text="편의점" />
              <CategoryItem text="음식점" />
              <CategoryItem text="외식" />
              <CategoryItem text="영화" />
              <CategoryItem text="공연" />
              <CategoryItem text="미용/뷰티" />
              <CategoryItem text="레저" />
              <CategoryItem text="명품" />
              <CategoryItem text="주류" />
              <CategoryItem text="의약품" />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className="w-full h-[32px] left-0 top-0">
                <h2 className="text-left w-[154.03px] h-[32px] left-0 top-0 font-pretendard font-semibold text-[15px] leading-[18px] text-black">
                  썸네일 업로드
                </h2>
              </div>
            </div>
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="w-full mt-2"
            />
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <CustomCalendar />
          {/* 캘린더 선택이벤트 추가 후 날짜 뽑아내기*/}
        </div>

        {/* 결제 버튼 */}
        <div className="w-full justify-center flex flex-row">
          <button className="longButton" onClick={handleSubmit}>
            챌린지 생성
          </button>
        </div>
      </div>

      <NavBar />
    </>
  );
}
