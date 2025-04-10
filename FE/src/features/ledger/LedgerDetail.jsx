import { useEffect, useState } from "react";
import MonthBar from "./components/MonthBar";
import Container from "@/components/Container";
import LedgerHeader from "./components/LedgerHeader";
import CategoryBox from "./components/CategoryBox";
import IsLoading from "@/components/IsLoading";
import AlertModal from "@/components/AlertModal";
import CustomBackHeader from "../../components/CustomBackHeader";
import Api from "../../services/Api";
import CategoryList from "./components/CategoryList";
import WasteIcon from "./assets/waste_icon.png";
import EmptyIcon from "./assets/empty_icon.png";
// import DeleteIcon from "./assets/delete_icon.png"; // 삭제 아이콘 추가 (아이콘 필요)

// 카테고리 아이콘 관련 컴포넌트
const categories = CategoryList();

export default function LedgerDetail() {
  const [alertModalProps, setAlertModalProps] = useState(null);
  const [alertMessage, setAlertMessage] = useState("");
  const [isAlertModal, setIsAlertModal] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [activeDate, setActiveDate] = useState(new Date());
  const [activeCategory, setActiveCategory] = useState("all");
  const [selectedMonth, setSelectedMonth] = useState({
    totalIncome: 0,
    totalSpent: 0,
    transactionsMap: [],
  });
  const [totalIncome, setTotalIncome] = useState(0);
  const [totalSpent, setTotalSpent] = useState(0);
  // 낭비 체크 상태 (각 항목의 인덱스 기반)
  const [wasteStates, setWasteStates] = useState({});
  // 낭비 항목만 보여줄지 여부
  const [showWasteOnly, setShowWasteOnly] = useState(false);
  // 수정 모드 상태
  const [isEditMode, setIsEditMode] = useState(false);
  // 삭제 모드 상태 추가
  const [isDeleteMode, setIsDeleteMode] = useState(false);
  // 수정 모달 상태
  const [isModalOpen, setIsModalOpen] = useState(false);
  // 현재 수정 중인 트랜잭션
  const [currentTransaction, setCurrentTransaction] = useState(null);

  // 수정 중인 데이터
  const [editData, setEditData] = useState({
    amount: 0,
    transactionDate: "",
    selectedDay: 1, // 선택된 일자
    selectedMonth: 1, // 선택된 월
    categoryId: 0,
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

  // 편집 모드 상태 (메인 편집 버튼용)
  const [isMainEditMode, setIsMainEditMode] = useState(false);

  // 역방향 카테고리 매핑 (카테고리명 -> categoryId)
  const reverseCategoryMapping = {};
  categories.forEach((cat) => {
    reverseCategoryMapping[cat.name] = cat.id;
  });

  useEffect(() => {
    const fetchMonthlyData = async () => {
      try {
        const year = activeDate.getFullYear();
        const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");
        const response = await Api.get(
          `api/payment/consumption/monthly?year=${year}&month=${month}`
        );
        console.log(response.data);
        if (response.data.status === "success") {
          const data = response.data.data;
          setIsLoading(false);

          // 거래 내역을 날짜 기준으로 정렬 (최신 순)
          if (data.transactionsMap && Array.isArray(data.transactionsMap)) {
            data.transactionsMap.sort((a, b) => {
              return new Date(b.date) - new Date(a.date);
            });

            // ✅ 낭비 여부를 transactionId 기준으로 저장
            const wasteMap = {};
            data.transactionsMap.forEach((item) => {
              // API가 snake_case 로 주면 is_waste, camelCase 면 isWaste
              const flag = item.isWaste ?? item.is_waste;
              wasteMap[item.transactionId] = Boolean(flag);
            });
            setWasteStates(wasteMap);
            setSelectedMonth(data);
          }
          setTotalIncome(data.totalIncome);
          setTotalSpent(data.totalSpent);
        }
      } catch (error) {
        console.error("월별 세부내역 조회 실패", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchMonthlyData();
  }, [activeDate]);

  // 카테고리 매핑
  const categoryMapping = {
    food: "식비",
    housing: "주거/통신",
    goods: "잡화",
    entertainment: "문화/여가",
    medical: "병원/약국",
    shopping: "쇼핑/미용",
    transportation: "교통/차량",
    income: "수입",
    spense: "결제",
  };

  // 카테고리 필터 적용
  const filteredTransactions =
    activeCategory === "all"
      ? selectedMonth.transactionsMap
      : selectedMonth.transactionsMap?.filter((item) => {
          return item.categoryName === categoryMapping[activeCategory];
        }) || [];

  // 만약 낭비 항목만 보고자 할 경우 필터 추가
  const transactionsToDisplay = showWasteOnly
    ? filteredTransactions.filter(
        (item) => wasteStates[item.transactionId] === true
      )
    : filteredTransactions;

  // 낭비 체크 버튼을 클릭 시 처리 (API 호출 포함)
  const handleWasteToggle = async (e, item) => {
    e.stopPropagation(); // 이벤트 버블링 방지

    // 낭비항목체크/ 내역수정-내역삭제용 변수
    const transactionId = item.transactionId;
    setWasteStates((prev) => ({
      ...prev,
      [transactionId]: !prev[transactionId],
    }));

    console.log("트랜스테스트", transactionId);
    const payload = {
      payment_history_id: transactionId,
    };
    console.log("API 요청 데이터:", payload);
    try {
      const response = await Api.post("api/payment/waste", payload);
      console.log("낭비 소비 체크 API 응답:", response.data);
    } catch (error) {
      console.error("에러 응답:", error.response?.data || error);
      setWasteStates((prev) => ({
        ...prev,
        [transactionId]: !prev[transactionId],
      }));
    }
  };

  // 결제내역 수정 함수 - 수정됨
  const fetchEditData = async (transactionId, payload) => {
    try {
      // 날짜 데이터를 변경
      const originalDate = new Date(payload.transactionDate);
      const newDate = new Date(
        originalDate.getFullYear(),
        payload.selectedMonth - 1, // JavaScript의 월은 0부터 시작
        payload.selectedDay,
        originalDate.getHours(),
        originalDate.getMinutes(),
        originalDate.getSeconds()
      );

      // ISO 형식의 날짜로 변환
      const formattedDate = newDate.toISOString().slice(0, 19);

      // 최종 페이로드 구성
      const finalPayload = {
        amount: payload.amount,
        transactionDate: formattedDate,
        categoryId: payload.categoryId,
        merchantName: payload.merchantName,
      };

      console.log("수정 요청 ID:", transactionId);
      console.log("전송할 데이터:", finalPayload);

      const response = await Api.put(
        `api/transactions/${transactionId}`,
        finalPayload
      );
      console.log("내역 수정 완료:", response.data);
      setIsLoading(true);

      // 성공 후 데이터 다시 불러오기
      await refreshData();
      setIsLoading(false);

      return true;
    } catch (error) {
      console.error("내역 수정 실패:", error);
      console.error("에러 상세:", error.response?.data || error.message);
      return false;
    }
  };

  // 데이터 새로고침 함수
  const refreshData = async () => {
    console.log("refreshData() 호출됨");
    try {
      const year = activeDate.getFullYear();
      const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");
      const refreshResponse = await Api.get(
        `api/payment/consumption/monthly?year=${year}&month=${month}`
      );

      if (refreshResponse.data.status === "success") {
        const data = refreshResponse.data.data;
        setIsLoading(true);

        // 배열이 있는지 확인하고, 없으면 빈 배열로 기본값 설정
        const transactionsMapArray = Array.isArray(data.transactionsMap)
          ? data.transactionsMap
          : [];

        console.log("응답된 transactionsMap:", transactionsMapArray);
        // 날짜별 정렬
        transactionsMapArray.sort((a, b) => {
          return new Date(b.date) - new Date(a.date);
        });
        // 낭비 상태 업데이트
        const wasteMap = {};
        transactionsMapArray.forEach((item) => {
          const flag = item.isWaste ?? item.is_waste;
          wasteMap[item.transactionId] = Boolean(flag);
        });
        setWasteStates(wasteMap);
        setSelectedMonth(data);
        setTotalIncome(data.totalIncome);
        setTotalSpent(data.totalSpent);
        setActiveCategory(activeCategory);
      }
    } catch (error) {
      console.error("데이터 새로고침 실패:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // 항목 클릭 시 모달 열기 또는 삭제 확인 모달 표시
  const handleTransactionClick = (item) => {
    if (!isEditMode && !isDeleteMode) return;

    setCurrentTransaction(item);

    if (isDeleteMode) {
      // 삭제 모드일 때는 확인 모달 표시
      setAlertModalProps({
        title: "내역 삭제",
        content: `"${
          item.merchantName || "내역"
        }" (${item.amount.toLocaleString()}원)을 삭제하시겠습니까?`,
        height: 250,
        showCancelButton: true,
        onConfirm: () => deleteTransaction(item.transactionId),
        onCancel: () => setAlertModalProps(null),
      });
      return;
    }

    // 수정 모드일 때는 수정 모달 열기
    // 날짜 포맷 변환 (yyyy-MM-ddTHH:mm:ss)
    const date = new Date(item.date);
    const formattedDate = `${date.getFullYear()}-${String(
      date.getMonth() + 1
    ).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}T${String(
      date.getHours()
    ).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}:00`;

    setEditData({
      amount: item.amount,
      transactionDate: formattedDate,
      selectedDay: date.getDate(),
      selectedMonth: date.getMonth() + 1,
      categoryId: reverseCategoryMapping[item.categoryName] || 1,
      merchantName: item.merchantName || "",
    });
    setIsModalOpen(true);
  };

  // 내역 삭제 함수 - 수정
  const deleteTransaction = async (transactionId) => {
    try {
      console.log("삭제 요청 ID:", transactionId);
      const response = await Api.delete(`api/transactions/${transactionId}`);
      console.log("내역 삭제 완료:", response.data);
      // 즉시 로컬 상태를 업데이트하여 삭제를 반영
      setSelectedMonth((prevState) => {
        const updatedTransactions = prevState.transactionsMap.filter(
          (item) => item.transactionId !== transactionId
        );
        return {
          ...prevState,
          transactionsMap: updatedTransactions,
        };
      });

      // 낭비 상태에서도 이 거래를 제거
      setWasteStates((prev) => {
        const newWasteStates = { ...prev };
        delete newWasteStates[transactionId];
        return newWasteStates;
      });
      await refreshData();
      // 확인 모달 닫기
      AlertModalOpen("내역이 삭제되었습니다.");
    } catch (error) {
      console.error("내역 삭제 실패:", error);
      console.error("에러 상세:", error.response?.data || error.message);
      AlertModalOpen("내역 삭제에 실패했습니다.");
    } finally {
      setAlertModalProps(null);
    }
  };

  // 카테고리 변경 핸들러 - 수정용
  const handleCategoryChange = (categoryId) => {
    setEditData((prev) => ({
      ...prev,
      categoryId: categoryId,
    }));
  };

  // 날짜 변경 핸들러 (월) - 수정용
  const handleMonthChange = (month) => {
    setEditData((prev) => ({
      ...prev,
      selectedMonth: parseInt(month),
    }));
  };

  // 날짜 변경 핸들러 (일) - 수정용
  const handleDayChange = (day) => {
    setEditData((prev) => ({
      ...prev,
      selectedDay: parseInt(day),
    }));
  };

  // 모달 저장 버튼 핸들러 - 수정
  const handleSaveChanges = async () => {
    if (!currentTransaction) return;

    const success = await fetchEditData(
      currentTransaction.transactionId,
      editData
    );

    if (success) {
      setIsModalOpen(false);
      setCurrentTransaction(null);
      setIsLoading(false);
    }
  };

  // 에딧 모드 토글
  const toggleEditMode = () => {
    setIsEditMode(!isEditMode);
    if (isDeleteMode) setIsDeleteMode(false);
    if (isModalOpen) {
      setIsModalOpen(false);
      setCurrentTransaction(null);
    }
  };

  // 삭제 모드 토글
  const toggleDeleteMode = () => {
    setIsDeleteMode(!isDeleteMode);
    if (isEditMode) setIsEditMode(false);
    if (isModalOpen) {
      setIsModalOpen(false);
      setCurrentTransaction(null);
    }
  };

  // 메인 편집 모드 토글
  const toggleMainEditMode = () => {
    setIsMainEditMode(!isMainEditMode);
    // 편집 모드를 끌 때는 다른 모드도 모두 초기화
    if (isMainEditMode) {
      setIsEditMode(false);
      setIsDeleteMode(false);
      if (isModalOpen) {
        setIsModalOpen(false);
        setCurrentTransaction(null);
      }
    }
  };

  // 해당 월의 일수를 구하는 함수
  const getDaysInMonth = (month) => {
    // 현재 연도의 해당 월의 마지막 날짜를 구함
    const date = new Date();
    const year = date.getFullYear();
    return new Date(year, month, 0).getDate();
  };

  // 월 선택 옵션 생성
  const monthOptions = Array.from({ length: 12 }, (_, i) => i + 1);

  // 일 선택 옵션 생성 (선택된 월에 따라 동적으로 변경)
  const dayOptions = Array.from(
    { length: getDaysInMonth(editData.selectedMonth) },
    (_, i) => i + 1
  );

  return (
    <>
      {!isLoading ? (
        <div className="h-screen overflow-y-auto">
          <Container>
            <CustomBackHeader title="가계부" />
            <LedgerHeader
              onEditClick={toggleMainEditMode}
              isEditMode={isMainEditMode}
              onEdit={toggleEditMode}
              onDelete={toggleDeleteMode}
              isEditModeOn={isEditMode}
              isDeleteModeOn={isDeleteMode}
            />

            <div className="w-full bg-white rounded-lg shadow-sm flex flex-col gap-3 pb-4 ">
              <MonthBar
                activeDate={activeDate}
                setActiveDate={setActiveDate}
                onYearMonthChange={({ year, month }) => {
                  console.log("선택된 연/월:", year, month);
                }}
              />
              <CategoryBox
                activeCategory={activeCategory}
                setActiveCategory={setActiveCategory}
              />
            </div>
            {/* 총 수입 지출 박스 */}
            <div className="w-full bg-white rounded-lg shadow-sm py-2">
              <div className="flex gap-6 justify-center">
                <div>
                  <p className="text-[#A2A2A2] text-[22px]">총 수입</p>
                  <p className="text-[#64C9F5] text-[22px] font-semibold">
                    {totalIncome.toLocaleString()}
                  </p>
                </div>
                <div>
                  <p className="text-[#A2A2A2] text-[22px]">총 지출</p>
                  <p className="text-[#FF957A] text-[22px] font-semibold">
                    {totalSpent.toLocaleString()}
                  </p>
                </div>
              </div>
            </div>

            {/* 수입/지출 요약 카드 */}
            <div className="w-full bg-white rounded-lg shadow-sm p-4 flex flex-col gap-2 mt-4">
              {/* 제목 + 낭비 버튼 수평 정렬 */}
              <div className="flex justify-between items-center">
                <p className="text-2xl text-gray-800">
                  {activeDate.getMonth() + 1}월
                </p>

                {/* 낭비 항목 필터 버튼 */}
                <button
                  onClick={() => setShowWasteOnly((prev) => !prev)}
                  className={` px-4 py-2 rounded transition-colors ${
                    showWasteOnly ? "whiteButton" : "blackButton"
                  }`}
                >
                  {showWasteOnly ? "전체 내역" : "낭비 내역"}
                </button>
              </div>

              {/* 모드 안내 메시지 */}
              {(isEditMode || isDeleteMode) && (
                <div
                  className={`mt-2 p-2 rounded text-white ${
                    isEditMode ? "bg-blue-500" : "bg-red-500"
                  }`}
                >
                  <p className="text-center">
                    {isEditMode
                      ? "✏️ 수정하려는 항목을 클릭하세요."
                      : "🗑️ 삭제하려는 항목을 클릭하세요."}
                  </p>
                </div>
              )}

              {/* 상세 내역 */}
              <ul>
                {transactionsToDisplay &&
                  transactionsToDisplay.map((item, index) => {
                    const matchedCategory = categories.find(
                      (cat) => cat.name === item.categoryName
                    );
                    const Icon = matchedCategory?.Icon;

                    const dateObj = new Date(item.date);
                    const formattedDate = `${
                      dateObj.getMonth() + 1
                    }/${dateObj.getDate()}`;

                    const isWaste = wasteStates[item.transactionId] || false;

                    return (
                      <li
                        key={index}
                        className={`flex items-center justify-between text-md mb-2 rounded-lg ${
                          isEditMode || isDeleteMode
                            ? "cursor-pointer hover:bg-gray-100"
                            : ""
                        } ${isDeleteMode ? "border border-red-300" : ""}`}
                        onClick={() => handleTransactionClick(item)}
                      >
                        {/* 왼쪽: 아이콘 + 날짜 + 상호명 */}
                        <div className="flex items-center gap-1">
                          {Icon && (
                            <img
                              src={Icon}
                              alt={item.category || item.categoryName}
                              className="w-9 h-auto mr-[5px]"
                            />
                          )}
                          <span>{formattedDate}</span>
                          <span className="ml-3">
                            {item.merchantName || "-"}
                          </span>
                        </div>

                        {/* 오른쪽: 금액 + 낭비 아이콘 또는 삭제 아이콘 */}
                        <div className="flex items-center">
                          <div className="min-w-[100px] font-semibold text-gray-800 text-right pr-5">
                            {item.amount.toLocaleString()}
                          </div>
                          {!isEditMode && !isDeleteMode && (
                            <img
                              src={isWaste ? WasteIcon : EmptyIcon}
                              alt="낭비 체크"
                              onClick={(e) => handleWasteToggle(e, item)}
                              className={`w-6 h-6 cursor-pointer transition-all duration-300 ${
                                isWaste ? "animate-pop" : ""
                              }`}
                            />
                          )}
                          {isDeleteMode && (
                            <div className="w-6 h-6 flex items-center justify-center">
                              <svg
                                className="w-5 h-5 text-red-500"
                                fill="currentColor"
                                viewBox="0 0 20 20"
                                xmlns="http://www.w3.org/2000/svg"
                              >
                                <path
                                  fillRule="evenodd"
                                  d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                                  clipRule="evenodd"
                                />
                              </svg>
                            </div>
                          )}
                        </div>
                      </li>
                    );
                  })}
              </ul>
            </div>
          </Container>
          {/* 수정 모달 */}
          {isModalOpen && currentTransaction && (
            <div className="fixed inset-0 bg-[#525252]/40 flex items-end justify-center z-50 pb-8">
              <div className="bg-white w-full rounded-t-xl p-4 animate-slide-up h-[610px] overflow-y-auto mb-safe">
                <h3 className="text-xl font-bold mb-4 mt-2">내역 수정</h3>

                {/* 카테고리 선택 */}
                <div className="mb-4">
                  {/* <p className="text-gray-600 mb-2">카테고리</p> */}
                  <div className="grid grid-cols-5 gap-2">
                    {categories.map((category) => (
                      <div
                        key={category.id}
                        onClick={() => handleCategoryChange(category.id)}
                        className={`flex flex-col items-center p-2 rounded-lg cursor-pointer ${
                          editData.categoryId === category.id
                            ? "bg-blue-100 border border-blue-400"
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
                    value={editData.merchantName}
                    onChange={(e) =>
                      setEditData({ ...editData, merchantName: e.target.value })
                    }
                    className="w-full p-2 border rounded-lg"
                    placeholder="상호명 입력"
                  />
                </div>

                {/* 금액 입력 - 수정 모달 */}
                <div className="relative mb-4">
                  <label className="block text-gray-600 mb-1">금액</label>
                  <input
                    type="text"
                    inputMode="numeric"
                    value={
                      editData.amount > 0
                        ? `${editData.amount.toLocaleString("ko-KR")}원`
                        : ""
                    }
                    onFocus={(e) => {
                      // 포커스 받으면 초기화하여 입력 준비
                      e.target.value = "";
                    }}
                    onChange={(e) => {
                      // 숫자만 입력 가능하도록
                      const raw = e.target.value.replace(/[^\d]/g, "");
                      setEditData({
                        ...editData,
                        amount: raw ? Number(raw) : 0,
                      });
                    }}
                    onBlur={(e) => {
                      // 포커스를 잃었을 때 다시 포맷팅
                      if (editData.amount > 0) {
                        e.target.value = `${editData.amount.toLocaleString(
                          "ko-KR"
                        )}원`;
                      }
                    }}
                    className="w-full p-2 border rounded-lg"
                    placeholder="금액 입력"
                  />
                </div>

                {/* 거래일자 선택 (월/일 선택) */}
                <div className="mb-6">
                  <label className="block text-gray-600 mb-1">거래일자</label>
                  <div className="flex gap-2">
                    {/* 월 선택 */}
                    <div className="flex-1">
                      <select
                        value={editData.selectedMonth}
                        onChange={(e) => handleMonthChange(e.target.value)}
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
                        value={editData.selectedDay}
                        onChange={(e) => handleDayChange(e.target.value)}
                        className="w-full p-2 border rounded-lg appearance-none bg-white"
                      >
                        {dayOptions.map((day) => (
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
                    onClick={() => setIsModalOpen(false)}
                    className="flex-1 h-[45px] bg-gray-200! rounded-lg font-medium text-lg flex items-center justify-center"
                  >
                    취소
                  </button>
                  <button
                    onClick={handleSaveChanges}
                    className="flex-1 h-[45px] text-white rounded-lg font-medium text-lg flex items-center justify-center"
                  >
                    저장
                  </button>
                </div>
              </div>
            </div>
          )}
          {/* 확인 모달 */}
          {alertModalProps && (
            <AlertModal
              title={alertModalProps.title}
              isOpen={true}
              isClose={() => setAlertModalProps(null)}
              height={180}
              onConfirm={() => {
                alertModalProps.onConfirm?.();
                setAlertModalProps(null);
              }}
              onCancel={() => {
                alertModalProps.onCancel?.();
                setAlertModalProps(null);
              }}
              showCancelButton={alertModalProps.showCancelButton}
            >
              <p className="text-center">{alertModalProps.content}</p>
            </AlertModal>
          )}
          {isAlertModal && (
            <AlertModal
              title="알림"
              isOpen={true}
              isClose={AlertModalClose}
              height={180}
            >
              <p className="text-center">{alertMessage}</p>
            </AlertModal>
          )}
          {/* 애니메이션 스타일 */}
          <style>{`
@keyframes pop {
  0% {
    transform: scale(0.5);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}
.animate-pop {
  animation: pop 0.4s ease-out;
}

@keyframes slide-up {
  0% {
    transform: translateY(100%);
  }
  100% {
    transform: translateY(0);
  }
}
.animate-slide-up {
  animation: slide-up 0.3s ease-out forwards;
}

`}</style>
        </div>
      ) : (
        <IsLoading />
      )}
    </>
  );
}
