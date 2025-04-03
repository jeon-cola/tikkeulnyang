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
import PaymentDetails from "./components/PaymentDetails";
import BellIcon from "./assets/Bell.png"

  const formatDate = (date) =>
    date.toLocaleDateString("en-CA", { timeZone: "Asia/Seoul" });

export default function SharedLedger() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [value, setValue] = useState(new Date());
  const [calendarData, setCalendarData] = useState([]);
  const [viewingNickname, setViewingNickname] = useState(""); // 🔥 현재 보고 있는 사람 닉네임
  const [selectedDate, setSelectedDate] = useState(null); // 날짜 선택 상태
  const [selectedUserId, setSelectedUserId] = useState(null); // 내 or 친구 ID
  const [isOpen, setIsOPen] = useState(false)
  const [ blinkList, setBlinkList ] = useState([])

  const emojiMap = {
    0: <img src={BlueFish} alt="BlueFish" className="w-5 mx-auto" />,
    1: <img src={GoldFish} alt="GoldFish" className="w-5 mx-auto" />,
    2: <img src={BlackFish} alt="BlackFish" className="w-5 mx-auto" />,
  };

  // 알림 조회
  function alamHandler() {
    const year = value.getFullYear();
    const month = value.getMonth() + 1;
    const fetchData = async () => {
      try {
        const response = await Api.get(`api/share/notification/dates?year=${year}&month=${month}`)
        if (response.data.status === "success") {
          const data = response.data.data.dates
          setBlinkList(data)
          console.log(data)
        }
      } catch (error) {
        console.log(error)
      }
    }
    fetchData()
  }

  // ✅ 기본 내 가계부 조회
  const fetchMyLedger = async () => {
    const year = value.getFullYear();
    const month = value.getMonth() + 1;
    try {
      const res = await Api.get(
        `api/share/myledger?year=${year}&month=${month}`
      );
      const fetchedData = res.data.data.data;
      setSelectedUserId(null)
      setCalendarData(fetchedData);
      setViewingNickname(null)
    } catch (err) {
      console.error("공유 가계부 캘린더 이모지 로딩 실패:", err);
    }
  };

  // ✅ 친구 가계부 조회
  const fetchUserLedger = async (userId) => {
    setIsOPen(false)
    const year = value.getFullYear();
    const month = value.getMonth() + 1;
    console.log(value)
    console.log(year,month,userId)
    try {
      const res = await Api.get(
        `/api/share/ledger/user/${userId}?year=${year}&month=${month}`
      );
      const fetchedData = res.data.data.data;
      const user = userId
      setSelectedUserId(user)
      setCalendarData(fetchedData);
      setViewingNickname(res.data.data.ownerNickname); // 🔥 현재 보고 있는 사람 이름 표시
    } catch (err) {
      console.error("상대방 가계부 로딩 실패:", err);
    }
  };

  // 공유 중 달이 바꼈을때 업데이트
  useEffect(() => {
    const fetchLedgerDetails = async () => {
      if (!selectedDate) return
      if (!selectedUserId) return fetchMyLedger()
      try {
        const year = selectedDate.getFullYear()
        const month = selectedDate.getMonth()+1
        const res = await Api.get(
          `/api/share/ledger/user/${selectedUserId}?year=${year}&month=${month}`
        );
        if (res.data.status === "success") {
          const fetchedData = res.data.data
          setCalendarData(fetchedData.data)
          setViewingNickname(fetchedData.ownerNickname)
        }
      } catch (err) {
        console.error("paymentDetails 조회 실패:", err);
      }
    };
    fetchLedgerDetails();
  }, [selectedDate, selectedUserId]);

  useEffect(() => {
    fetchMyLedger();
    alamHandler()
  }, []);

  return (
    <div className="w-full mb-[30px]">
      <Container >
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
              onActiveStartDateChange={({activeStartDate,view})=>{
                if (view === "month") {
                  setValue(activeStartDate)
                  setSelectedDate(activeStartDate);
                  alamHandler(activeStartDate)
                }
              }}
              className="z-0"
              value={value}
              onChange={(date) => {
                setValue(date);
                const formatted = formatDate(date);
                setSelectedDate((prev) => (prev === formatted ? null : formatted));
                setIsOPen(true)
              }}
              tileContent={({ date, view }) => {
                if (view === "month") {
                  if (date > new Date()) return null; // 오늘이 아닌 데이터 표시하지 않음

                  const formatted = date.toLocaleDateString("en-CA");
                  const entry = calendarData.find(
                    (item) => item.date === formatted
                  );
                  const alam = Array.isArray(blinkList) ? blinkList.find(
                    (item) => item === formatted
                  )
                  : null
                  return entry && alam ? (
                    <div className="relative w-full h-full z-[5]" style={{ transform: 'translateY(-50%)', position: 'absolute', top: '30px', right: '0px' }}>
                      <div className="absolute bottom-2 right-4">
                        {emojiMap[entry.emoji] || ""}
                      </div>
                      <span className="animate-pulse bg-red-500 absolute top-2 right-4 w-[10px] h-[10px] rounded-full z-[10]"></span>
                    </div>
                  ) : entry ? (
                    <div className="mt-4 text-center text-[18px]">
                      {emojiMap[entry.emoji] || ""}
                    </div>
                  ) : alam?(
                    <div className="relative w-full h-full z-[5]" style={{ transform: 'translateY(-50%)', position: 'absolute', top: '30px', right: '0px' }}>
                      <span className="animate-pulse bg-red-500 absolute top-2 right-4 w-[10px] h-[10px] rounded-full z-[10]"></span>
                    </div>
                  ) : ""
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
                onClick={()=>{
                  setIsOPen(false)
                  fetchMyLedger()
                }}
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
        {isOpen
        ? <PaymentDetails type="share" date={selectedDate} userId={selectedUserId}/>
        : ""
        }
      </Container>
    </div>
  );
}
