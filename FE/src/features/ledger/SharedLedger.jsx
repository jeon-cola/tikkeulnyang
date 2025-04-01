import { useEffect, useState } from "react";
import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import LedgerHeader from "./components/LedgerHeader";
import AddUser from "./assets/add_user.png";
import BlackCat from "./assets/ledger_cat.png";
import Modal from "@/components/Modal";
import Api from "@/services/Api";
import InviteLinkSection from "./components/budget/InviteLinkSection";
import ProfileImageList from "./components/budget/ProfileImageList";

export default function SharedLedger() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [value, setValue] = useState(new Date());
  const [calendarData, setCalendarData] = useState([]);

  const emojiMap = {
    0: "🙂",
    1: "😄",
    2: "😓",
  };

  // ✅ 기본 내 가계부 조회
  const fetchMyLedger = async () => {
    const year = value.getFullYear();
    const month = value.getMonth() + 1;
    try {
      const res = await Api.get(
        `api/share/myledger?year=${year}&month=${month}`
      );
      const fetchedData = res.data.data.data;
      setCalendarData(fetchedData);
    } catch (err) {
      console.error("공유 가계부 캘린더 이모지 로딩 실패:", err);
    }
  };

  // ✅ 친구 가계부 조회
  const fetchUserLedger = async (userId) => {
    const year = value.getFullYear();
    const month = value.getMonth() + 1;
    try {
      const res = await Api.get(
        `/api/share/ledger/user/${userId}?year=${year}&month=${month}`
      );
      const fetchedData = res.data.data.data;
      setCalendarData(fetchedData);
    } catch (err) {
      console.error("상대방 가계부 로딩 실패:", err);
    }
  };

  useEffect(() => {
    fetchMyLedger();
  }, [value]);

  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />

        <div className="relative">
          {/* 상단 버튼 영역 */}
          <div className="flex items-center justify-between px-2">
            <img
              className="w-[20%] cursor-pointer"
              src={AddUser}
              alt="사용자 추가"
              onClick={() => setIsModalOpen(true)}
            />
            <img className="w-[20%] h-auto" src={BlackCat} alt="캣 이미지" />
          </div>

          {/* ✅ 공유된 사용자 프로필 리스트 */}
          <div className="px-2 py-3">
            <ProfileImageList onClick={fetchUserLedger} />
          </div>

          {/* 🗓️ 캘린더 */}
          <CustomCalendar
            className="z-0"
            value={value}
            onChange={(date) => setValue(date)}
            tileContent={({ date, view }) => {
              if (view === "month") {
                const formatted = date.toLocaleDateString("en-CA");
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

          {/* 🔗 초대 링크 모달 */}
          {isModalOpen && (
            <Modal
              title="사용자 초대"
              description="초대 링크를 복사하여 친구에게 보내세요."
              onClose={() => setIsModalOpen(false)}
            >
              <InviteLinkSection />
            </Modal>
          )}
        </div>
      </Container>
    </div>
  );
}
