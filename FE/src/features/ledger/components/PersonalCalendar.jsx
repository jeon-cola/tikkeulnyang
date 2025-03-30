import { useEffect, useState } from "react";
import CustomCalendar from "@/components/CustomCalendar";
import PaymentDetails from "./PaymentDetails";
import Api from "../../../services/Api";

export default function PersonalLedgerCalendar() {
  const [value, setValue] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const [calendarData, setCalendarData] = useState([]);

  const handleDateClick = (date) => {
    const formatted = date.toISOString().split("T")[0];
    // 토글 기능: 이미 선택된 날짜면 닫기
    const newSelected = selectedDate === formatted ? null : formatted;
    setSelectedDate(newSelected);

    // 스크롤 이동: 해당 날짜 셀을 달력 위로 올림
    setTimeout(() => {
      const target = document.getElementById(`date-${formatted}`);
      if (target) {
        target.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    }, 50);
  };

  useEffect(() => {
    const fetchCalendarData = async () => {
      try {
        const response = await Api.get("api/payment/consumption");
        const fetchedData = response.data.data.data;
        const formattedData = fetchedData.map((item) => ({
          date: item.date,
          income: item.income,
          spending: item.expense,
          transactions: item.transactions,
        }));
        setCalendarData(formattedData);
      } catch (error) {
        console.error("캘린더 데이터 로딩 중 에러 발생:", error);
      }
    };
    fetchCalendarData();
  }, []);

  return (
    <div>
      <CustomCalendar
        value={value}
        onChange={(date) => {
          setValue(date);
          handleDateClick(date);
        }}
        tileContent={({ date, view }) => {
          if (view === "month") {
            const formattedDate = date.toLocaleDateString("en-CA", {
              timeZone: "Asia/Seoul",
            });
            const entry = calendarData.find(
              (item) => item.date === formattedDate
            );
            return entry ? (
              <div
                id={`date-${formattedDate}`}
                className="flex flex-col items-center mt-3 text-[9px] relative z-10"
              >
                {entry.income > 0 && (
                  <span className="text-[#37C7EF]">
                    +{entry.income.toLocaleString()}
                  </span>
                )}
                {entry.spending > 0 && (
                  <span className="text-[#FF886B]">
                    -{entry.spending.toLocaleString()}
                  </span>
                )}
              </div>
            ) : null;
          }
          return null;
        }}
        data={calendarData}
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
