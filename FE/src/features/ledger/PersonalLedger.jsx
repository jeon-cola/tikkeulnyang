import { useEffect, useState } from "react";
import { Outlet } from "react-router-dom";
import Container from "@/components/Container";
import PersonalCalendar from "./components/PersonalCalendar";
import LedgerHeader from "./components/LedgerHeader";
import Api from "../../services/Api";
import CategoryList from "./components/CategoryList";
// import Cat from "./assets/black_cat.png";

const categories = CategoryList();

export default function PersonalLedger() {
  // 내역 추가 모달 및 로딩 상태 관리
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isCreateModeOn, setIsCreateModeOn] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // 거래 생성에 필요한 데이터 상태 (중복 선언 제거)
  const [createData, setCreateData] = useState({
    amount: 0,
    transactionDate: "",
    selectedDay: new Date().getDate(),
    selectedMonth: new Date().getMonth() + 1,
    categoryId: 2, // 기본 카테고리: 식비
    merchantName: "",
  });

  // 캘린더 갱신을 위한 상태 (토글로 처리)
  const [refreshCalendar, setRefreshCalendar] = useState(false);

  // 데이터 새로고침 함수 (필요한 로직 구현)
  const refreshData = async () => {
    // 거래 내역을 다시 불러오는 로직
  };

  // 해당 월의 일수를 구하는 함수
  const getDaysInMonth = (month) => {
    const year = new Date().getFullYear();
    return new Date(year, month, 0).getDate();
  };

  // 월 선택 옵션 (1~12월)
  const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1);

  // 일 선택 옵션 (현재 선택된 월 기준)
  const dayOptions = Array.from(
    { length: getDaysInMonth(createData.selectedMonth) },
    (_, i) => i + 1
  );

  // 카테고리 변경 핸들러 (추가용)
  const handleAddCategoryChange = (categoryId) => {
    setCreateData((prev) => ({
      ...prev,
      categoryId: categoryId,
    }));
  };

  // 월 변경 핸들러 (추가용)
  const handleAddMonthChange = (month) => {
    setCreateData((prev) => ({
      ...prev,
      selectedMonth: parseInt(month),
    }));
  };

  // 일 변경 핸들러 (추가용)
  const handleAddDayChange = (day) => {
    setCreateData((prev) => ({
      ...prev,
      selectedDay: parseInt(day),
    }));
  };

  // 내역 추가 시작 함수: 현재 날짜를 기준으로 생성 데이터를 초기화하고 모달을 엽니다.
  const startAddTransaction = () => {
    const now = new Date();
    const formattedDate = `${now.getFullYear()}-${String(
      now.getMonth() + 1
    ).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}T${String(
      now.getHours()
    ).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}:00`;

    setCreateData({
      amount: 0,
      transactionDate: formattedDate,
      selectedDay: now.getDate(),
      selectedMonth: now.getMonth() + 1,
      categoryId: 2,
      merchantName: "",
    });

    setIsAddModalOpen(true);
    setIsCreateModeOn(true);
  };

  // 내역 추가 실행 함수: API 호출 후 상태 업데이트
  const executeAddTransaction = async () => {
    const now = new Date();
    const payload = {
      cardId: 0,
      transactionType: 2, // 1: 수입, 2: 지출
      amount: createData.amount,
      categoryId: createData.categoryId,
      merchantName: createData.merchantName,
      year: now.getFullYear(),
      month: createData.selectedMonth,
      day: createData.selectedDay,
    };

    try {
      const response = await Api.post(`api/transactions`, payload);
      console.log("새 거래 내역 생성:", response.data);
      setIsLoading(true);

      // 데이터 새로고침 (필요한 경우 로직 구현)
      await refreshData();

      // 캘린더 반영을 위해 refreshCalendar 상태 변경 (토글)
      setRefreshCalendar((prev) => !prev);
      // 모달 닫기 및 생성 모드 해제
      setIsAddModalOpen(false);
      setIsCreateModeOn(false);

      alert("새 내역이 추가되었습니다.");
      setIsLoading(false);
    } catch (error) {
      console.error("새 거래 내역 생성 실패:", error);
      alert("새 거래 내역 추가에 실패했습니다.");
    }
  };

  return (
    <div className="w-full relative">
      <Outlet />
      <Container>
        <LedgerHeader />
        <PersonalCalendar refreshTrigger={refreshCalendar} />
      </Container>
      {/* + 버튼에 onClick 이벤트 추가 */}
      <div
        onClick={startAddTransaction}
        className="fixed right-[20px] bottom-[80px] z-50 bg-[#FF957A] w-10 h-10 rounded-full flex items-center justify-center text-white shadow-md cursor-pointer"
      >
        <p className="text-[40px] pb-[7px]">+</p>
      </div>
      {/* 내역 추가 모달 */}
      {isAddModalOpen && (
        <div className="fixed inset-0 bg-[#525252]/40 flex items-end justify-center z-50 pb-8">
          <div className="bg-white w-full rounded-t-xl p-4 animate-slide-up h-[650px] overflow-y-auto mb-safe">
            <h3 className="text-xl font-bold mb-4 mt-2">내역 추가</h3>
            {/* 카테고리 선택 영역 */}
            <div className="mb-4">
              <div className="grid grid-cols-5 gap-2">
                {categories.map((category) => (
                  <div
                    key={category.id}
                    onClick={() => handleAddCategoryChange(category.id)}
                    className={`flex flex-col items-center p-2 rounded-lg cursor-pointer ${
                      createData.categoryId === category.id
                        ? "bg-green-100 border border-green-400"
                        : "bg-gray-50"
                    }`}
                  >
                    <img
                      src={category.Icon}
                      alt={category.name}
                      className="w-10 h-10 mb-1"
                    />
                    <span className="text-[10px] text-center">
                      {category.name}
                    </span>
                  </div>
                ))}
              </div>
            </div>
            {/* 상호명 입력 */}
            <div className="mb-4">
              <label className="block text-gray-600 mb-1">상호명</label>
              <input
                type="text"
                value={createData.merchantName}
                onChange={(e) =>
                  setCreateData({
                    ...createData,
                    merchantName: e.target.value,
                  })
                }
                className="w-full p-2 border rounded-lg"
                placeholder="상호명 입력"
              />
            </div>
            {/* 금액 입력 */}
            <div className="mb-4">
              <label className="block text-gray-600 mb-1">금액</label>
              <input
                type="text"
                value={
                  createData.amount === 0
                    ? "0"
                    : String(createData.amount).replace(/^0+/, "")
                }
                onChange={(e) => {
                  if (e.target.value === "") {
                    setCreateData({ ...createData, amount: 0 });
                    return;
                  }
                  if (/^\d*$/.test(e.target.value)) {
                    const cleanValue = e.target.value.replace(/^0+(\d)/, "$1");
                    setCreateData({
                      ...createData,
                      amount: Number(cleanValue),
                    });
                  }
                }}
                className="w-full p-2 border rounded-lg"
                placeholder="금액 입력"
              />
            </div>
            {/* 거래일자 선택 (월/일) */}
            <div className="mb-6">
              <label className="block text-gray-600 mb-1">거래일자</label>
              <div className="flex gap-2">
                {/* 월 선택 */}
                <div className="flex-1">
                  <select
                    value={createData.selectedMonth}
                    onChange={(e) => handleAddMonthChange(e.target.value)}
                    className="w-full p-2 border rounded-lg appearance-none bg-white"
                  >
                    {monthOptions.map((month) => (
                      <option key={month} value={month}>
                        {month}월
                      </option>
                    ))}
                  </select>
                </div>
                {/* 일 선택 */}
                <div className="flex-1">
                  <select
                    value={createData.selectedDay}
                    onChange={(e) => handleAddDayChange(e.target.value)}
                    className="w-full p-2 border rounded-lg appearance-none bg-white"
                  >
                    {Array.from(
                      { length: getDaysInMonth(createData.selectedMonth) },
                      (_, i) => i + 1
                    ).map((day) => (
                      <option key={day} value={day}>
                        {day}일
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
            {/* 버튼 영역 */}
            <div className="flex gap-2 mt-8 pb-4">
              <button
                onClick={() => {
                  setIsAddModalOpen(false);
                  setIsCreateModeOn(false);
                }}
                className="flex-1 py-4 bg-gray-200 rounded-lg font-medium text-lg"
              >
                취소
              </button>
              <button
                onClick={executeAddTransaction}
                className="flex-1 py-4 bg-green-500 text-white rounded-lg font-medium text-lg"
              >
                추가
              </button>
            </div>
          </div>
        </div>
      )}
      {/* 애니메이션 스타일 */}
      <style>{`
        @keyframes slide-up {
          0% { transform: translateY(100%); }
          100% { transform: translateY(0); }
        }
        .animate-slide-up { animation: slide-up 0.3s ease-out forwards; }
      `}</style>
    </div>
  );
}
