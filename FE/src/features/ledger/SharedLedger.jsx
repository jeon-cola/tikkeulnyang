import { useEffect, useState } from "react";
import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import LedgerHeader from "./components/LedgerHeader";
import AddUser from "./assets/add_user.png";
import BlackCat from "./assets/ledger_cat.png";
import Api from "@/services/Api";

export default function SharedLedger() {
  const [value, setValue] = useState(new Date());
  const [calendarData, setCalendarData] = useState([]);

  const emojiMap = {
    0: "🙂", // 평범
    1: "😄", // 만족
    2: "😓", // 낭비
  };

  useEffect(() => {
    const fetchMyLedger = async () => {
      const year = value.getFullYear();
      const month = value.getMonth() + 1;
      try {
        const res = await Api.get(
          `api/share/myledger?year=${year}&month=${month}`
        );
        const fetchedData = res.data.data.data; // 날짜별 이모지 리스트
        setCalendarData(fetchedData);
      } catch (err) {
        console.error("공유 가계부 캘린더 이모지 로딩 실패:", err);
      }
    };

    fetchMyLedger();
  }, [value]);

  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />
        <div className="relative">
          <div className="flex">
            <img className="w-[20%]" src={AddUser} alt="사용자 추가" />
            <img
              className="absolute -top-1 right-3 z-10 w-[20%] h-auto"
              src={BlackCat}
              alt="캣 이미지"
            />
          </div>
          <CustomCalendar
            className="z-0"
            value={value}
            onChange={(date) => setValue(date)}
            tileContent={({ date, view }) => {
              if (view === "month") {
                const formatted = date.toLocaleDateString("en-CA"); // "2025-03-01"
                const entry = calendarData.find(
                  (item) => item.date === formatted
                );
                return entry ? (
                  <div className="mt-2 text-center text-[18px]">
                    {emojiMap[entry.emoji] || ""}
                  </div>
                ) : null;
              }
              return null;
            }}
          />
        </div>
      </Container>
    </div>
  );
}
