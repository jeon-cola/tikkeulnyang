import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import CustomCalendar from "@/components/CustomCalendar";
// import PaymentDetails from "./PaymentDetails";
import Api from "../../../services/Api";

export default function PersonalLedgerCalendar() {
  const [value, setValue] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const [calendarData, setCalendarData] = useState([]);
  const navigate = useNavigate();

  const handleDateClick = (date) => {
    const formatted = date.toISOString().split("T")[0];
    setSelectedDate(selectedDate === formatted ? null : formatted);
  };

  useEffect(() => {
    const fetchCalendarData = async () => {
      try {
        const response = await Api.get("api/payment/consumption");
        console.log("서버 데이터:", response.data.data);
        // response.data.data.data에 날짜 배열이 있음
        const fetchedData = response.data.data.data;
        // API의 expense를 spending으로 변환하여 달력에서 사용
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
    <div className="relative max-w-[320px]">
      {/* 상단 우측 블랙 버튼 */}
      <button
        className="blackButton absolute top-2 right-2 z-10"
        onClick={() => navigate("/ledger/detail")}
      >
        세부내역
      </button>

      {/* CustomCalendar에 API 데이터를 prop으로 전달 */}
      <CustomCalendar
        value={value}
        onChange={(date) => {
          setValue(date);
          handleDateClick(date);
        }}
        tileContent={({ date, view }) => {
          if (view === "month") {
            const formattedDate = date.toISOString().split("T")[0];
            const entry = calendarData.find(
              (item) => item.date === formattedDate
            );
            return entry ? (
              <div className="flex flex-col text-xs">
                {entry.income > 0 && (
                  <span className="text-blue-500">+{entry.income}</span>
                )}
                {entry.spending > 0 && (
                  <span className="text-red-500">-{entry.spending}</span>
                )}
              </div>
            ) : null;
          }
          return null;
        }}
        data={calendarData}
      />

      {/* 선택된 날짜가 있을 경우 결제 정보 표시 */}
      {/* {selectedDate && (
        <div className="mt-4">
          <PaymentDetails date={selectedDate} />
        </div>
      )} */}
    </div>
  );
}
