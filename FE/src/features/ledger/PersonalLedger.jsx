import { useEffect, useState } from "react";
import { Outlet } from "react-router-dom";
import Container from "@/components/Container";
import PersonalCalendar from "./components/PersonalCalendar";
import LedgerHeader from "./components/LedgerHeader";
import Api from "../../services/Api";
import CategoryList from "./components/CategoryList";
import AlertModal from "@/components/AlertModal";

const categories = CategoryList();

export default function PersonalLedger() {
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isCreateModeOn, setIsCreateModeOn] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshCalendar, setRefreshCalendar] = useState(false);

  const [isAlertModal, setIsAlertModal] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");

  const [createData, setCreateData] = useState({
    amount: 0,
    transactionDate: "",
    selectedDay: new Date().getDate(),
    selectedMonth: new Date().getMonth() + 1,
    categoryId: 2,
    merchantName: "",
  });

  const AlertModalOpen = (message) => {
    setAlertMessage(message);
    setIsAlertModal(true);
  };

  const AlertModalClose = () => {
    setIsAlertModal(false);
    setAlertMessage("");
  };

  const getDaysInMonth = (month) => {
    const year = new Date().getFullYear();
    return new Date(year, month, 0).getDate();
  };

  const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1);

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

  const executeAddTransaction = async () => {
    if (!createData.merchantName.trim()) {
      AlertModalOpen("상호명을 입력해주세요.");
      return;
    }

    if (createData.amount <= 0) {
      AlertModalOpen("금액은 0원보다 커야 합니다.");
      return;
    }

    const now = new Date();
    const payload = {
      cardId: 0,
      transactionType: 2,
      amount: createData.amount,
      categoryId: createData.categoryId,
      merchantName: createData.merchantName,
      year: now.getFullYear(),
      month: createData.selectedMonth,
      day: createData.selectedDay,
    };

    try {
      const response = await Api.post("api/transactions", payload);
      console.log("새 거래 내역 생성:", response.data);

      setIsLoading(true);
      setRefreshCalendar((prev) => !prev);
      setIsAddModalOpen(false);
      setIsCreateModeOn(false);

      AlertModalOpen("새 내역이 추가되었습니다.");
    } catch (error) {
      console.error("거래 생성 실패", error);
      AlertModalOpen("거래 추가에 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full relative">
      <Outlet />
      <Container>
        <LedgerHeader />
        <PersonalCalendar refreshTrigger={refreshCalendar} />
      </Container>

      {/* + 버튼 */}
      <div
        onClick={startAddTransaction}
        className="fixed right-[20px] bottom-[80px] z-50 bg-[#FF957A] w-10 h-10 rounded-full flex items-center justify-center text-white shadow-md cursor-pointer"
      >
        <p className="text-[40px] pb-[7px]">+</p>
      </div>

      {/* 내역 추가 모달 */}
      {isAddModalOpen && (
        <div className="fixed inset-0 bg-[#525252]/40 flex items-end justify-center z-50 pb-8">
          <div className="bg-white w-full rounded-t-xl p-4 animate-slide-up h-[610px] overflow-y-auto mb-safe">
            <h3 className="text-xl font-bold mb-4 mt-2">내역 추가</h3>

            {/* 카테고리 선택 */}
            <div className="mb-4">
              <div className="grid grid-cols-5 gap-2">
                {categories.map((category) => (
                  <div
                    key={category.id}
                    onClick={() =>
                      setCreateData((prev) => ({
                        ...prev,
                        categoryId: category.id,
                      }))
                    }
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
                  setCreateData({ ...createData, merchantName: e.target.value })
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
                inputMode="numeric"
                value={
                  createData.amount === 0
                    ? ""
                    : `${createData.amount.toLocaleString("ko-KR")}원`
                }
                onChange={(e) => {
                  const raw = e.target.value.replace(/[^\d]/g, "");
                  if (raw === "") {
                    setCreateData({ ...createData, amount: 0 });
                    return;
                  }
                  if (/^\d+$/.test(raw)) {
                    setCreateData({ ...createData, amount: Number(raw) });
                  }
                }}
                className="w-full p-2 border rounded-lg"
                placeholder="금액 입력"
              />
            </div>

            {/* 거래일자 선택 */}
            <div className="mb-6">
              <label className="block text-gray-600 mb-1 text-sm">
                거래일자
              </label>
              <div className="flex gap-2">
                <div className="flex-1">
                  <select
                    value={createData.selectedMonth}
                    onChange={(e) =>
                      setCreateData((prev) => ({
                        ...prev,
                        selectedMonth: parseInt(e.target.value),
                      }))
                    }
                    className="w-full h-[40px] text-sm p-1.5 border rounded-lg bg-white"
                  >
                    {monthOptions.map((month) => (
                      <option key={month} value={month}>
                        {month}월
                      </option>
                    ))}
                  </select>
                </div>
                <div className="flex-1">
                  <select
                    value={createData.selectedDay}
                    onChange={(e) =>
                      setCreateData((prev) => ({
                        ...prev,
                        selectedDay: parseInt(e.target.value),
                      }))
                    }
                    className="w-full h-[40px] text-sm p-1.5 border rounded-lg bg-white"
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

            {/* 버튼 */}
            <div className="flex gap-2 mt-8 pb-4">
              <button
                onClick={() => {
                  setIsAddModalOpen(false);
                  setIsCreateModeOn(false);
                }}
                className="flex-1 h-[45px] bg-gray-200! rounded-lg font-medium text-lg flex items-center justify-center"
              >
                취소
              </button>
              <button
                onClick={executeAddTransaction}
                className="flex-1 h-[45px] text-white rounded-lg font-medium text-lg flex items-center justify-center"
              >
                추가
              </button>
            </div>
          </div>
        </div>
      )}

      {/* AlertModal */}
      {isAlertModal && (
        <div className="fixed inset-0 z-[999]">
          <AlertModal
            title="알림"
            isOpen={isAlertModal}
            isClose={AlertModalClose}
            height={180}
          >
            <div className="text-center">
              <p className="mb-2">{alertMessage}</p>
            </div>
          </AlertModal>
        </div>
      )}

      {/* 애니메이션 */}
      <style>{`
        @keyframes slide-up {
          0% { transform: translateY(100%); }
          100% { transform: translateY(0); }
        }
        .animate-slide-up {
          animation: slide-up 0.3s ease-out forwards;
        }
      `}</style>
    </div>
  );
}
