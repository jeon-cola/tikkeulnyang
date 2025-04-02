import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import CustomCalendar from "@/components/CustomCalendar";
import PaymentDetails from "./PaymentDetails";
import Api from "../../../services/Api";

export default function PersonalLedgerCalendar() {
  const navigate = useNavigate();
  const [value, setValue] = useState(new Date());
  const [selectedDate, setSelectedDate] = useState(null);
  const [calendarData, setCalendarData] = useState([]);


  const formatDate = (date) =>
    date.toLocaleDateString("en-CA", { timeZone: "Asia/Seoul" });

  const handleDateClick = (date) => {
    const formatted = formatDate(date);
    setSelectedDate((prev) => (prev === formatted ? null : formatted));

    setTimeout(() => {
      const target = document.getElementById(`date-${formatted}`);
      if (target) {
        target.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    }, 50);
  };

  // 초기 달력 데이터 수집
  useEffect(() => {
    const fetchCalendarData = async () => {
      try {
        const year = value.getFullYear();
        const month = value.getMonth() + 1;
        const response = await Api.get("api/payment/consumption", {
          params: { year, month },
        });
        console.log(response.data)
        if (response.data.status === "success"){
          const fetchedData = response.data.data.data;
          const formattedData = fetchedData.map((item) => ({
            date: item.date,
            income: item.income,
            spending: item.expense,
            transactions: item.transactions,
          }));
          setCalendarData(formattedData);
        }
      } catch (error) {
        console.error("캘린더 데이터 로딩 중 에러 발생:", error);
      }
    };

    fetchCalendarData();
  }, [value]);

  const tileContent = useCallback(
    ({ date, view }) => {
      if (view !== "month") return null;

      const formattedDate = formatDate(date);
      const entry = calendarData.find((item) => item.date === formattedDate);
      if (!entry) return null;

      return (
        <div
          id={`date-${formattedDate}`}
          className="flex flex-col items-center mt-3 text-[9px]"
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
      );
    },
    [calendarData]
  );

  return (
    <div className="relative">
      <CustomCalendar
        value={value}
        onChange={(date) => {
          console.log("값 변경")
          setValue(date);
          handleDateClick(date);
        }}
        onActiveStartDateChange={({ activeStartDate, view }) => {
          console.log("달력 월 변경:", activeStartDate);
          console.log("년도:", activeStartDate.getFullYear(), "월:", activeStartDate.getMonth() + 1);
          // 월 변경 시 데이터 가져오기
          if (view === 'month') {
            setValue(activeStartDate);
          }
        }}
        tileContent={tileContent}
      />

      <button
        className="blackButton absolute right-4 top-4"
        onClick={() => navigate("/ledger/detail")}
      >
        세부내역
      </button>

      {selectedDate && (
        <div className="mt-4">
          <PaymentDetails date={selectedDate} />
        </div>
      )}
    </div>
  );
}
