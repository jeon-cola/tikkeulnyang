import CustomHeader from "@/components/CustomHeader";
import NavBar from "@/components/NavBar";
import { useNavigate } from "react-router-dom";
import CustomCalendar from "@/components/CustomCalendar";
import { ChallengeService } from "@/features/challenge/services/ChallengeService";
import { useState, useEffect } from "react";
import ChallengeCalendar from "@/features/challenge/components/ChallengeCalendar";
import CustomBackHeader from "@/components/CustomBackHeader";

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
    //uploadThumbnail(challengeId);
    console.log("thumbnail", thumbnail);
  }, [thumbnail]);

  // 요청값 입력 함수
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setChallengeData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // ChallengeCalendar에서 선택된 날짜 범위 처리 함수
  const handleDateRangeChange = (dateRange) => {
    if (dateRange[0]) {
      setChallengeData((prev) => ({
        ...prev,
        startDate: dateRange[0],
      }));
    }

    if (dateRange[1]) {
      setChallengeData((prev) => ({
        ...prev,
        endDate: dateRange[1],
      }));
    }
  };

  /**
   * 챌린지 방을 생성한뒤, 응답으로 받은 challengeId 값을 바탕으로 썸네일을 S3에 생성
   */
  const handleSubmit = async () => {
    try {
      // 구조분해할당으로 필드 추출 및 빈 값 확인
      const fieldsToCheck = [
        { value: challengeData.challengeName, name: "챌린지 제목" },
        { value: challengeData.description, name: "챌린지 소개" },
        { value: challengeData.targetAmount, name: "목표 금액" },
        { value: challengeData.startDate, name: "시작일" },
        { value: challengeData.endDate, name: "종료일" },
        { value: challengeData.limitAmount, name: "예치금 최소 금액" },
        { value: challengeData.challengeCategory, name: "챌린지 카테고리" },
        { value: challengeData.maxParticipants, name: "참가 최대 인원" },
      ];

      // 빈 필드 확인
      const emptyFields = fieldsToCheck
        .filter((field) => !field.value)
        .map((field) => field.name);

      // 빈 필드가 있는 경우
      if (emptyFields.length > 0) {
        alert(`다음 항목을 입력해주세요:\n${emptyFields.join("\n")}`);
        return;
      }

      // 썸네일이 선택되지 않은 경우 확인
      if (!thumbnail) {
        alert("썸네일 이미지를 업로드해주세요.");
        return;
      }

      console.log("challengeData", challengeData);
      const response = await ChallengeService.postChallengeCreate(
        challengeData
      );

      // 챌린지 생성 직후 바로 썸네일 업로드
      try {
        if (thumbnail) {
          await ChallengeService.postChallengeThumbnail(
            response.challengeId,
            thumbnail
          );
          console.log("썸네일 업로드 성공");
        }
      } catch (error) {
        console.error("썸네일 업로드 실패:", error);
        alert("썸네일 업로드에 실패했습니다.");
      }

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
      <CustomBackHeader title="챌린지 생성" />
      <div className="flex flex-col items-start p-[0px_0px_82px] gap-3 absolute w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px]">
            {/* 챌린지 제목 */}
            <div className="mt-3 text-left font-semibold text-xl leading-[18px] text-black">
              챌린지 제목
            </div>

            {/* 입력창 */}
            <div className="box-border mt-5 mb-2 w-full h-[38px] bg-white border border-[#DFDFDF] rounded-[6px] flex-none">
              <input
                type="text"
                name="challengeName"
                value={challengeData.challengeName}
                onChange={handleInputChange}
                placeholder="챌린지 제목을 입력해주세요"
                className="w-full h-full px-[5px] pt-[5px] font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>

            <div className="mt-5 text-left font-semibold text-xl leading-[18px] text-black">
              챌린지 소개
            </div>

            <div className="mt-5 box-border w-full h-40 bg-white border border-[#DFDFDF] rounded-[6px] order-1 flex-none">
              <textarea
                name="description"
                value={challengeData.description}
                onChange={handleInputChange}
                placeholder="소개글을 입력해주세요"
                className="w-full h-[168px] resize-none px-[5px] pt-[5px] font-thin text-[20px] leading-[32px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px] h-[70px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                {/* 예치금 최소 금액 설정 */}
                <h2 className="text-left font-semibold text-xl leading-[18px] text-black">
                  예치금 최소 금액 설정
                </h2>
              </div>
            </div>

            {/* 입력창 */}
            <div className="box-border w-full h-[38px] bg-white border border-[#DFDFDF] rounded-[6px] order-1 flex-none">
              <input
                type="number"
                name="limitAmount"
                value={challengeData.limitAmount}
                onChange={handleInputChange}
                placeholder="최소 1,000원부터 입력"
                className="w-full h-full px-[5px] pt-[5px] font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>

          <div className="flex flex-col items-start p-0 w-[347px] h-[70px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                <h2 className="text-left font-semibold text-xl leading-[18px] text-black">
                  목표 금액 설정
                </h2>
              </div>
            </div>

            {/* 목표 금액 설정 */}
            <div className="box-border w-full h-[38px] bg-white border border-[#DFDFDF] rounded-[6px]">
              <input
                type="number"
                name="targetAmount"
                value={challengeData.targetAmount}
                onChange={handleInputChange}
                placeholder="기간내 목표 금액 설정"
                className="w-full h-full px-[5px] pt-[5px] font-pretendard font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>

          <div className="flex flex-col items-start p-0 w-[347px] h-[70px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                <h2 className="text-left font-semibold text-xl leading-[18px] text-black">
                  참가 최대 인원 설정
                </h2>
              </div>
            </div>

            {/* 최대인원 설정 */}
            <div className="box-border w-full h-[38px] bg-white border border-[#DFDFDF] rounded-[6px] order-1 flex-none">
              <input
                type="number"
                name="maxParticipants"
                value={challengeData.maxParticipants}
                onChange={handleInputChange}
                placeholder="참가 최대인원 입력"
                className="w-full h-full px-[5px] pt-[5px] font-thin text-[20px] leading-[24px] placeholder-[#DFDFDF] text-black focus:outline-none bg-transparent"
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px]">
            <div className="w-full h-[32px] order-none flex-none">
              <div className=" w-full h-[32px] left-0 top-0">
                {/* 챌린지 카테고리 설정 */}
                <h2 className="text-left font-semibold text-xl leading-[18px] text-black">
                  챌린지 카테고리 설정
                </h2>
              </div>
            </div>

            {/* 챌린지 목록 */}
            <div className="w-full pt-3 overflow-hidden flex flex-row overflow-x-auto whitespace-nowrap items-center p-0 gap-[10px] relative w-[543px] h-auto">
              <CategoryItem text="주유" />
              <CategoryItem text="쇼핑" />
              <CategoryItem text="버스" />
              <CategoryItem text="지하철" />
              <CategoryItem text="택시" />
              <CategoryItem text="학원" />
              <CategoryItem text="통신" />
              <CategoryItem text="해외" />
              <CategoryItem text="카페" />
              <CategoryItem text="편의점" />
              <CategoryItem text="음식점" />
              <CategoryItem text="병원" />
              <CategoryItem text="배달" />
              <CategoryItem text="결제" />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <div className="flex flex-col items-start p-0 w-[347px]">
            <div className="w-full  order-none flex-none">
              <div className="w-full  left-0 top-0">
                <div className="text-left pt-4 font-semibold text-xl leading-[18px] text-black">
                  썸네일 업로드
                </div>
              </div>
            </div>
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="w-full mt-4 mb-4"
            />
          </div>
        </div>

        <div className="flex flex-col items-center p-[12px_20px_12px] gap-[22px] relative w-full h-auto bg-white rounded-[6px]">
          <ChallengeCalendar onDateRangeChange={handleDateRangeChange} />
          {/* 캘린더 선택이벤트 추가 후 날짜 뽑아내기*/}
        </div>

        {/* 결제 버튼 */}
        <div className="w-full justify-center flex flex-row">
          <button className="longButton text-white" onClick={handleSubmit}>
            챌린지 생성
          </button>
        </div>
      </div>

      <NavBar />
    </>
  );
}
