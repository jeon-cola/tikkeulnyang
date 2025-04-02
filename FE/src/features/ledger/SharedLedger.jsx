import { useEffect, useState } from "react";
import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import LedgerHeader from "./components/LedgerHeader";
import AddUser from "./assets/shared/add_user.png";
import BlackCat from "./assets/ledger_cat.png";
import BlueFish from "./assets/shared/blue_fish.png";
import GoldFish from "./assets/shared/gold_fish.png";
import BlackFish from "./assets/shared/black_fish.png";
import InviteModal from "./components/InviteModal";
import Api from "@/services/Api";
import InviteLinkSection from "./components/InviteLinkSection";
import ProfileImageList from "./components/ProfileImageList";

import EntertainmentIcon from "./assets/category/entertainment_icon.png";
import FoodIcon from "./assets/category/food_icon.png";
import GoodsIcon from "./assets/category/goods_icon.png";
import HousingIcon from "./assets/category/housing_icon.png";
import MedicalIcon from "./assets/category/medical_icon.png";
import ShoppingIcon from "./assets/category/shopping_icon.png";
import TransportationIcon from "./assets/category/transportation_icon.png";
import IncomeIcon from "./assets/category/income_icon.png";
import SpenseIcon from "./assets/category/spense_icon.png";
import EducationIcon from "./assets/category/education_icon.png";

const categories = [
  { id: 1, name: "주거/통신", Icon: HousingIcon },
  { id: 2, name: "식비", Icon: FoodIcon },
  { id: 3, name: "교통/차량", Icon: TransportationIcon },
  { id: 4, name: "교육/육아", Icon: EducationIcon },
  { id: 5, name: "쇼핑/미용", Icon: ShoppingIcon },
  { id: 6, name: "병원/약국", Icon: MedicalIcon },
  { id: 7, name: "문화/여가", Icon: EntertainmentIcon },
  { id: 8, name: "잡화", Icon: GoodsIcon },
  { id: 9, name: "결제", Icon: SpenseIcon },
  { name: "수입", Icon: IncomeIcon },
];

export default function SharedLedger() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [value, setValue] = useState(new Date());
  const [calendarData, setCalendarData] = useState([]);
  const [viewingNickname, setViewingNickname] = useState(""); // 🔥 현재 보고 있는 사람 닉네임
  const [selectedDate, setSelectedDate] = useState(null); // 날짜 선택 상태
  const [selectedUserId, setSelectedUserId] = useState(null); // 내 or 친구 ID
  const [paymentData, setPaymentData] = useState(null);

  const emojiMap = {
    0: <img src={GoldFish} alt="GoldFish" className="w-5 mx-auto" />,
    1: <img src={BlueFish} alt="BlueFish" className="w-5 mx-auto" />,
    2: <img src={BlackFish} alt="BlackFish" className="w-5 mx-auto" />,
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
      setViewingNickname(""); // 🔄 내 가계부일 땐 초기화
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
      setViewingNickname(res.data.data.ownerNickname); // 🔥 현재 보고 있는 사람 이름 표시
    } catch (err) {
      console.error("상대방 가계부 로딩 실패:", err);
    }
  };

  // 누군가의 가계부 세부 조회
  useEffect(() => {
    const fetchLedgerDetails = async (userId) => {
      if (!selectedDate) return;
      const targetUserId = selectedUserId || { userInfo }; // 리덕스에 올린 로그인한 유저 정보
      try {
        const res = await Api.get(
          `api/share/ledger/user/${selectedUserId}/daily/${selectedDate}`
        ); //date는 yyyy-mm-dd 형태
        // 여기서 userId가 로그인한 사용자라면 사용자의 세부내역
        // 친구 초대된 친구의 가계부 갔다면 그 친구의 세부내역을 페이먼트로 띄우고 싶습니다다
        csetPaymentData("user의 세부내역", res.data.data);
      } catch (err) {
        console.error("paymentDetails 조회 실패:", err);
      }
    };
    fetchLedgerDetails();
  }, [selectedDate, selectedUserId]);

  useEffect(() => {
    fetchMyLedger();
  }, [value]);

  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />

        <div className="relative">
          {/* 상단: 프로필 리스트와 AddUser 아이콘 한 줄 */}
          <div className="flex items-center justify-between px-4 py-2">
            {/* 왼쪽: ProfileImageList 다음에 AddUser 아이콘 */}
            <div className="flex items-center gap-3">
              <ProfileImageList onClick={fetchUserLedger} />
              <img
                className="w-10 h-10 cursor-pointer"
                src={AddUser}
                alt="사용자 추가"
                onClick={() => setIsModalOpen(true)}
              />
            </div>
          </div>

          {/* 달력 및 BlackCat 이미지 */}
          <div className="relative">
            <CustomCalendar
              className="z-0"
              value={value}
              onChange={(date) => {
                setValue(date);
                setSelectedDate(date.toISOString().split("T")[0]);
              }}
              tileContent={({ date, view }) => {
                if (view === "month") {
                  if (date > new Date()) return null; // 오늘이 아닌 데이터 표시하지 않음

                  const formatted = date.toLocaleDateString("en-CA");
                  const entry = calendarData.find(
                    (item) => item.date === formatted
                  );
                  return entry ? (
                    <div className="mt-4 text-center text-[18px]">
                      {emojiMap[entry.emoji] || ""}
                    </div>
                  ) : null;
                }
                return null;
              }}
            />
            {/* BlackCat: 달력 기준 우측 상단에 겹치게 배치 */}
            <img
              className="absolute top-0 right-0 z-10 w-12 h-auto"
              src={BlackCat}
              alt="캣 이미지"
            />
          </div>

          {viewingNickname && (
            <div className="text-center text-sm text-gray-600 mt-1">
              <span className="font-semibold">{viewingNickname}</span>님의
              가계부를 보고 있어요
              <button
                onClick={fetchMyLedger}
                className="ml-2 px-2 py-1 text-xs border rounded hover:bg-gray-100"
              >
                내 가계부로
              </button>
            </div>
          )}

          {/* 🔗 초대 링크 모달 */}
          {isModalOpen && (
            <InviteModal
              title="친구 초대 링크"
              description="초대 링크를 복사하여 친구에게 보내세요."
              onClose={() => setIsModalOpen(false)}
            >
              <div>
                <InviteLinkSection />
              </div>
            </InviteModal>
          )}
        </div>

        {/* 페이먼트 디테일 */}
        {paymentData && (
          <div className="bg-white w-full p-[10px] text-black">
            <p className="flex flex-start pb-[10px]">
              {formatKoreanDate(paymentData.date)}
            </p>

            <ul className="space-y-2">
              {paymentData.transactions.map((item, index) => {
                const matchedCategory = categories.find(
                  (cat) => cat.id === item.category
                );
                const Icon = matchedCategory ? matchedCategory.Icon : null;

                return (
                  <li key={index} className="flex items-center gap-2 text-sm">
                    {Icon && (
                      <img
                        src={Icon}
                        alt={item.category}
                        className="w-8 h-auto"
                      />
                    )}
                    <span className="ml-[20px]">{item.category}</span>
                    <span className="relative left-30px">
                      {item.matchedName}
                    </span>
                    <span>{item.description}</span>
                    <span className="ml-auto">
                      {item.amount != null
                        ? `${item.amount.toLocaleString()}`
                        : "금액 없음"}
                    </span>
                  </li>
                );
              })}
            </ul>
          </div>
        )}
      </Container>
    </div>
  );
}
