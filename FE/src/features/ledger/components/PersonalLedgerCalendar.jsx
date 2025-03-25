// components/PersonalLedgerCalendar.jsx
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import CustomCalendar from "@/components/CustomCalendar";
import PaymentDetails from "./PaymentDetails"; // 새로 만든 결제 정보 컴포넌트

export default function PersonalLedgerCalendar() {
  const [value, setValue] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const navigate = useNavigate();

  const handleDateClick = (date) => {
    const formatted = date.toISOString().split("T")[0];
    // 같은 날짜를 클릭하면 토글해서 닫기
    if (selectedDate === formatted) {
      setSelectedDate(null);
    } else {
      setSelectedDate(formatted);
    }
  };

  return (
    <div className="relative max-w-[320px]">
      {/* 상단 우측 블랙 버튼 */}
      <button
        className="blackButton absolute top-2 right-2 z-10"
        onClick={() => navigate("/ledger/detail")}
      >
        세부내역
      </button>

      {/* 기본 커스텀 캘린더 */}
      <CustomCalendar
        value={value}
        onChange={(date) => {
          setValue(date);
          handleDateClick(date);
        }}
      />

      {/* 선택된 날짜가 있을 경우 결제 정보 표시 */}
      {selectedDate && (
        <div className="mt-4">
          <PaymentDetails date={selectedDate} />
        </div>
      )}
    </div>
  );
}
