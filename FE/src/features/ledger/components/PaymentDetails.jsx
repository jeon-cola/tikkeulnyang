import { useEffect, useState } from "react";
import Api from "../../../services/Api";
import CategoryList from "./CategoryList";
import Comment from "./Comment";

const categories = CategoryList();

const formatKoreanDate = (dateStr) => {
  const date = new Date(dateStr);
  const day = date.getDate();
  const weekday = date.toLocaleDateString("ko-KR", { weekday: "long" });
  return `${day}일 ${weekday}`;
};

export default function PaymentDetails({ date, type, userId = null, onUse }) {
  const [paymentData, setPaymentData] = useState(null);
  const [blinkList, setBlinkList] = useState([]);
  const [memoModalOpen, setMemoModalOpen] = useState(false);
  const [memoContent, setMemoContent] = useState("");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentTransaction, setCurrentTransaction] = useState(null);
  const [editData, setEditData] = useState({
    rawAmount: "",
    transactionDate: "",
    selectedYear: 0,
    selectedMonth: 1,
    selectedDay: 1,
    categoryId: 0,
    merchantName: "",
  });
  const [isCommentOpen, setIsCommentOpen] = useState(false);

  // 메모 모달 닫기
  const closeMemoModal = () => {
    setMemoModalOpen(false);
  };

  // === 데이터 Fetch ===
  const fetchDetails = async () => {
    try {
      let payload;
      if (type === "share" && userId) {
        const res = await Api.get(
          `api/share/ledger/user/${userId}/daily/${date}`
        );
        if (res.data.status === "success") {
          payload = res.data.data;
        }
      } else {
        const res = await Api.get(`api/payment/consumption/daily/${date}`);
        payload = res.data.data || res.data;
      }
      setPaymentData(payload);
    } catch (e) {
      console.error("일별 세부내역 조회 실패", e);
    }
  };

  const fetchBlinkList = async () => {
    let year, month;
    if (typeof date === "string") [year, month] = date.split("-");
    else {
      const d = new Date(date);
      year = d.getFullYear();
      month = d.getMonth() + 1;
    }
    try {
      const res = await Api.get(
        `api/share/notification/dates?year=${year}&month=${month}`
      );
      if (res.data.status === "success") {
        setBlinkList(res.data.data.dates);
      }
    } catch (e) {
      console.error("알림 조회 실패", e);
    }
  };

  useEffect(() => {
    if (date) {
      fetchDetails();
      fetchBlinkList();
    }
  }, [date]);

  if (!paymentData) return <div>소비내역이 없습니다</div>;

  const hasBlink = blinkList.includes(paymentData.date);

  // === 수정 모달 열기/닫기 ===
  const openEditModal = (item) => {
    if (type !== "personal") return;

    let rawDate = paymentData.date;
    if (/^\d{4}-\d{2}-\d{2}$/.test(rawDate)) rawDate += "T00:00:00";
    let d = new Date(rawDate);
    if (isNaN(d.getTime())) d = new Date();

    const pad = (n) => String(n).padStart(2, "0");
    const iso =
      [d.getFullYear(), pad(d.getMonth() + 1), pad(d.getDate())].join("-") +
      "T" +
      [pad(d.getHours()), pad(d.getMinutes()), pad(d.getSeconds())].join(":");

    setCurrentTransaction(item);
    setEditData({
      rawAmount: String(item.amount),
      transactionDate: iso,
      selectedYear: d.getFullYear(),
      selectedMonth: d.getMonth() + 1,
      selectedDay: d.getDate(),
      categoryId: categories.find((c) => c.name === item.category)?.id || 0,
      merchantName: item.merchantName || "",
    });
    setIsModalOpen(true);
  };
  const closeEditModal = () => {
    setIsModalOpen(false);
    setCurrentTransaction(null);
  };

  // === 저장/삭제 로직 ===
  const doSave = async () => {
    const orig = new Date(editData.transactionDate);
    const newD = new Date(
      editData.selectedYear,
      editData.selectedMonth - 1,
      editData.selectedDay,
      orig.getHours(),
      orig.getMinutes(),
      orig.getSeconds()
    );
    const pad = (n) => String(n).padStart(2, "0");
    const iso =
      [newD.getFullYear(), pad(newD.getMonth() + 1), pad(newD.getDate())].join(
        "-"
      ) +
      "T" +
      [
        pad(newD.getHours()),
        pad(newD.getMinutes()),
        pad(newD.getSeconds()),
      ].join(":");

    await Api.put(`api/transactions/${currentTransaction.transactionId}`, {
      amount: Number(editData.rawAmount),
      transactionDate: iso,
      categoryId: editData.categoryId,
      merchantName: editData.merchantName,
    });
    await fetchDetails();
    closeEditModal();
  };

  const doDelete = async () => {
    await Api.delete(`api/transactions/${currentTransaction.transactionId}`);
    await fetchDetails();
    closeEditModal();
  };

  return (
    <div className="bg-white w-full p-[10px] text-black">
      {/* 상단 헤더 */}
      {type === "personal" ? (
        <div className="flex items-center justify-between text-lg mt-2">
          <p>{formatKoreanDate(paymentData.date)}</p>
          <button
            onClick={async () => {
              const res = await Api.post("/api/memos", { date });
              setMemoContent(res.data.content || "메모 비어있음");
              setMemoModalOpen(true);
            }}
            className="flex items-center bg-[#FF987A] hover:bg-[#ff8461] text-white text-sm font-medium px-3 py-1.5 rounded-full shadow"
          >
            <img
              src="/icons/white_cat.png"
              alt="cream cat"
              className="w-6 h-auto mr-1.5"
            />
            티끌냥 리포트
          </button>
        </div>
      ) : (
        <div className="flex justify-between items-center">
          <p>{formatKoreanDate(paymentData.date)}</p>
          <div
            className="relative cursor-pointer"
            onClick={() => setIsCommentOpen(true)}
          >
            {hasBlink && (
              <span className="absolute top-0 right-0 w-2 h-2 bg-red-500 rounded-full" />
            )}
            <Comment
              title="댓글"
              isOpen={isCommentOpen}
              onClose={() => {
                onUse();
                setIsCommentOpen(false);
              }}
              userId={userId}
              date={paymentData.date}
            />
          </div>
        </div>
      )}

      {/* 메모 모달 */}
      {memoModalOpen && (
        <div className="fixed inset-0 bg-[#525252]/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-lg w-[90%] max-w-md p-4 relative z-10">
            <div className="flex items-center justify-center gap-2 mb-2">
              <img
                src="/icons/cream_cat.png"
                alt="cream cat"
                className="w-6 h-auto"
              />
              <h2 className="text-lg font-bold">티끌냥 리포트</h2>
              <img
                src="/icons/cream_cat.png"
                alt="cream cat"
                className="w-6 h-auto"
              />
            </div>
            <p className="text-gray-800 whitespace-pre-line">
              {memoContent}
              <span className="inline-block ml-2">
                <img
                  src="/icons/cream_cat.png"
                  alt="cream cat"
                  className="w-4 h-auto inline-block align-text-bottom"
                />
                <img
                  src="/icons/cream_cat.png"
                  alt="cream cat"
                  className="w-4 h-auto inline-block align-text-bottom ml-1"
                />
                <img
                  src="/icons/cream_cat.png"
                  alt="cream cat"
                  className="w-4 h-auto inline-block align-text-bottom ml-1"
                />
              </span>
            </p>
            <div className="flex justify-center mt-4">
              <button
                onClick={closeMemoModal}
                className="px-4 py-1 bg-[#FF987A] text-white rounded hover:bg-[#ff8461]"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 거래 목록 */}
      <ul className="h-auto">
        {paymentData.transactions.map((item, idx) => (
          <li
            key={idx}
            onClick={() => openEditModal(item)}
            className={`flex items-center gap-2 text-lg pt-4 ${
              type === "personal" ? "cursor-pointer hover:bg-gray-100" : ""
            }`}
          >
            {categories.find((c) => c.name === item.category)?.Icon && (
              <img
                src={categories.find((c) => c.name === item.category).Icon}
                alt=""
                className="w-10 h-auto"
              />
            )}
            <span className="ml-[20px]">{item.category}</span>
            <span className="ml-2">{item.description}</span>
            <span className="ml-auto">{item.amount.toLocaleString()}원</span>
          </li>
        ))}
      </ul>

      {/* 수정 모달 */}
      {isModalOpen && currentTransaction && (
        <div className="fixed inset-0 bg-[#525252]/40 flex items-end justify-center z-50 pb-8">
          <div className="bg-white w-full rounded-t-xl p-4 animate-slide-up h-[650px] overflow-y-auto">
            <h3 className="text-xl font-bold mb-4 mt-2">내역 수정</h3>

            {/* 카테고리 선택 */}
            <div className="mb-4 grid grid-cols-5 gap-2">
              {categories.map((cat) => (
                <div
                  key={cat.id}
                  onClick={() =>
                    setEditData((d) => ({ ...d, categoryId: cat.id }))
                  }
                  className={`flex flex-col items-center p-2 rounded-lg cursor-pointer ${
                    editData.categoryId === cat.id
                      ? "bg-blue-100 border border-blue-400"
                      : "bg-gray-50"
                  }`}
                >
                  <img
                    src={cat.Icon}
                    alt={cat.name}
                    className="w-10 h-10 mb-1"
                  />
                  <span className="text-[10px] text-center">{cat.name}</span>
                </div>
              ))}
            </div>

            {/* 상호명 */}
            <div className="mb-4">
              <label className="block text-gray-600 mb-1">상호명</label>
              <input
                type="text"
                value={editData.merchantName}
                onChange={(e) =>
                  setEditData((d) => ({
                    ...d,
                    merchantName: e.target.value,
                  }))
                }
                className="w-full p-2 border rounded-lg"
                placeholder="상호명 입력"
              />
            </div>

            {/* 금액 */}
            <div className="relative mb-4">
              <label className="block text-gray-600 mb-1">금액</label>
              <input
                type="text"
                value={editData.rawAmount}
                onChange={(e) => {
                  const v = e.target.value.replace(/[^\d]/g, "");
                  setEditData((d) => ({ ...d, rawAmount: v }));
                }}
                placeholder="금액 입력"
                className="w-full p-2 border rounded-lg"
              />
            </div>

            {/* 거래일자 선택 */}
            <div className="mb-6">
              <label className="block text-gray-600 mb-1">거래일자</label>
              <div className="flex gap-2">
                <select
                  value={editData.selectedYear}
                  onChange={(e) =>
                    setEditData((d) => ({
                      ...d,
                      selectedYear: +e.target.value,
                    }))
                  }
                  className="p-2 border rounded-lg"
                >
                  {Array.from({ length: 5 }, (_, i) => {
                    const y = new Date().getFullYear() - 2 + i;
                    return (
                      <option key={y} value={y}>
                        {y}년
                      </option>
                    );
                  })}
                </select>
                <select
                  value={editData.selectedMonth}
                  onChange={(e) =>
                    setEditData((d) => ({
                      ...d,
                      selectedMonth: +e.target.value,
                    }))
                  }
                  className="p-2 border rounded-lg"
                >
                  {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                    <option key={m} value={m}>
                      {m}월
                    </option>
                  ))}
                </select>
                <select
                  value={editData.selectedDay}
                  onChange={(e) =>
                    setEditData((d) => ({
                      ...d,
                      selectedDay: +e.target.value,
                    }))
                  }
                  className="p-2 border rounded-lg"
                >
                  {Array.from(
                    {
                      length: new Date(
                        editData.selectedYear,
                        editData.selectedMonth,
                        0
                      ).getDate(),
                    },
                    (_, i) => i + 1
                  ).map((d) => (
                    <option key={d} value={d}>
                      {d}일
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* 버튼 */}
            <div className="flex gap-2 mt-8 pb-4">
              <button
                onClick={doDelete}
                className="flex-1 h-[45px] bg-red-500 text-white rounded-lg font-medium text-lg"
              >
                삭제
              </button>
              <button
                onClick={closeEditModal}
                className="flex-1 h-[45px] bg-gray-200! rounded-lg font-medium text-lg"
              >
                취소
              </button>
              <button
                onClick={doSave}
                className="flex-1 h-[45px] bg-blue-500! text-white rounded-lg font-medium text-lg"
              >
                저장
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
