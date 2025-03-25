import { useState } from "react";
import CustomCalendar from "@/components/CustomCalendar";
import Container from "@/components/Container";
import LedgerHeader from "./components/LedgerHeader";
import CategoryBox from "./components/CategoryBox";

export default function LedgerDetail() {
  const [calendarVisible, setCalendarVisible] = useState(false);
  const totalIncome = 0;
  const totalSpense = 0;

  return (
    <div>
      <Container>
        <LedgerHeader />
        <CategoryBox />

        {/* 수입/지출 요약 카드 */}
        <div className="w-full bg-white rounded-lg shadow-sm p-4 flex flex-col gap-3">
          <div className="flex justify-between items-center">
            {/* 기간 설정 버튼 */}
            <button
              className="blackButton text-[12px] px-3 py-[2px] rounded-full"
              onClick={() => setCalendarVisible((prev) => !prev)}
            >
              기간설정
            </button>

            {/* 수입/지출 */}
            <div className="flex gap-6 text-sm">
              <div className="text-right">
                <p className="text-[#A2A2A2] text-xs">총 수입</p>
                <p className="text-[#FF957A] font-semibold">{totalIncome}</p>
              </div>
              <div className="text-right">
                <p className="text-[#A2A2A2] text-xs">총 지출</p>
                <p className="text-[#64C9F5] font-semibold">{totalSpense}</p>
              </div>
            </div>
          </div>

          {/* 캘린더 토글 */}
          {calendarVisible && (
            <div className="mt-2 bg-white rounded-md shadow-md p-4">
              <CustomCalendar />
            </div>
          )}
        </div>

        {/* 기타 콘텐츠 (예: 상세 내역) */}
        <div className="w-full bg-white rounded-lg shadow-sm p-4 mt-4">
          <h1 className="text-base font-semibold text-gray-800">상세 내역</h1>
          {/* 여기에 상세 내역이 들어갑니다 */}
        </div>
      </Container>
    </div>
  );
}
