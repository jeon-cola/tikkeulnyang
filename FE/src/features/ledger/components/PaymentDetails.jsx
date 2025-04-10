import { useEffect, useState } from "react";
import Api from "../../../services/Api";
import CategoryList from "./CategoryList";
import Comment from "./Comment";

// 카테고리 아이콘 관련 컴포넌트
const categories = CategoryList();

const formatKoreanDate = (dateStr) => {
  const date = new Date(dateStr);
  const day = date.getDate(); // 날짜 (11)
  const weekday = date.toLocaleDateString("ko-KR", { weekday: "long" }); // 화요일
  return `${day}일 ${weekday}`;
};

export default function PaymentDetails({ date, type, userId = null, onUse }) {
  const [paymentData, setPaymentData] = useState(null);
  const [isOpen, setIsOPen] = useState(false);
  const [blinkList, setBlinkList] = useState([]);
  const [memoModalOpen, setMemoModalOpen] = useState(false);
  const [memoContent, setMemoContent] = useState("");

  // 알람 지우기
  function cancleAlamHandler() {
    const fetchData = async () => {
      try {
        const response = await Api.post(
          `api/share/notification/date/read/${date}`
        );
        console.log(response.data);
        if (response.data.status === "success") {
          alamHandler();
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchData();
  }

  //모달 닫기
  function onCloseModalHandler() {
    onUse();
    setIsOPen(false);
  }

  // 상세 내역 조회
  const fetchedPaymentData = async () => {
    try {
      // 상대방 아이디가 있고 공유일때만
      if (type === "share" && userId) {
        if (userId) {
          console.log(date);
          const response = await Api.get(
            `api/share/ledger/user/${userId}/daily/${date}`
          );
          if (response.data.status === "success") {
            setPaymentData(response.data.data);
          }
        }
      } else {
        const response = await Api.get(`api/payment/consumption/daily/${date}`);
        setPaymentData(response.data);
      }
    } catch (error) {
      console.error("일별 세부내역조회 실패", error);
    }
  };

  // 알림 조회
  function alamHandler() {
    let year, month, day;
    if (typeof date === "string") {
      [year, month, day] = date.split("-");
    } else if (date instanceof Date) {
      year = date.getFullYear();
      month = date.getMonth() + 1;
      day = date.getDate();
    } else {
      console.error("지원되지 않는 날짜 형식:", date);
    }
    const fetchData = async () => {
      try {
        const response = await Api.get(
          `api/share/notification/dates?year=${year}&month=${month}`
        );
        if (response.data.status === "success") {
          const data = response.data.data.dates;
          setBlinkList(data);
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchData();
  }

  const handleMemoButtonClick = async () => {
    try {
      const response = await Api.post("/api/memos", { date });
      if (response.status === 200 && response.data) {
        setMemoContent(response.data.content || "메모 내용이 비어 있습니다.");
      } else {
        setMemoContent("해당 날짜의 거래 내역이 없습니다.");
      }
    } catch (error) {
      console.error("메모 생성/조회 실패", error);
      setMemoContent("메모를 불러오는 데 실패했습니다.");
    }
    setMemoModalOpen(true);
  };

  const closeMemoModal = () => setMemoModalOpen(false);

  useEffect(() => {
    if (date) {
      console.log(date);
      fetchedPaymentData();
      alamHandler();
    }
  }, [date]); //data가 바뀔 때마다 다시 요청

  if (!paymentData) return <div>소비내역이 없습니다</div>;

  const match = blinkList.some((item) => item === date);

  return (
    <div
      className={`bg-white w-full p-[10px] text-black ${
        type === "personal" ? "mb-10" : ""
      }`}
    >
      {type === "personal" ? (
        <div className="flex items-center justify-between text-lg mt-2">
          <p>{formatKoreanDate(paymentData?.data?.date)}</p>
          {type === "personal" && new Date(date) <= new Date() && (
            <button
              onClick={handleMemoButtonClick}
              className="flex items-center bg-[#FF987A] hover:bg-[#ff8461] text-white text-sm font-medium px-3 py-1.5 rounded-full shadow"
            >
              <img
                src="/icons/white_cat.png"
                alt="고양이 아이콘"
                className="w-6 h-auto mr-1.5"
              />
              티끌냥의 리포트
            </button>
          )}
        </div>
      ) : (
        <div className="flex justify-between">
          <p className="flex flex-start pb-[10px]">
            {formatKoreanDate(
              !userId ? paymentData?.data?.date : paymentData?.date
            )}
          </p>
          {match ? (
            <div
              onClick={async () => {
                await cancleAlamHandler(), setIsOPen(true);
              }}
              className="relative"
            >
              <span className=" bg-red-500 absolute top-0 right-0 w-[8px] h-[8px] rounded-full" />
              <Comment
                title="댓글"
                isOpen={isOpen}
                onClose={onCloseModalHandler}
                userId={userId}
                date={!userId ? paymentData?.data?.date : paymentData?.date}
              />
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <div onClick={() => setIsOPen(true)}>
                <Comment
                  title="댓글"
                  isOpen={isOpen}
                  onClose={onCloseModalHandler}
                  userId={userId}
                  date={!userId ? paymentData?.data?.date : paymentData?.date}
                />
              </div>
              {/* <button
                className="bg-blue-500 hover:bg-blue-600 text-white text-sm px-3 py-1 rounded"
                onClick={handleMemoButtonClick}
              >
                티끌냥
              </button> */}
            </div>
          )}
        </div>
      )}

      {/* 메모 모달 */}
      {memoModalOpen && (
        <div className="fixed inset-0 bg-[#525252]/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-lg w-[90%] max-w-md p-4 relative z-10">
            <div className="flex items-center justify-center gap-2 mb-2">
              <img
                src="/icons/cream_cat.png"
                alt="cat icon"
                className="w-6 h-auto"
              />
              <h2 className="text-lg font-bold">티끌냥 리포트</h2>
              <img
                src="/icons/cream_cat.png"
                alt="cat icon"
                className="w-6 h-auto"
              />
            </div>
            <p className="text-gray-800 whitespace-pre-line">
              {memoContent}
              <span className="inline-block ml-2">
                <img
                  src="/icons/cream_cat.png"
                  alt="cat icon"
                  className="w-4 h-auto inline-block align-text-bottom"
                />
                <img
                  src="/icons/cream_cat.png"
                  alt="cat icon"
                  className="w-4 h-auto inline-block align-text-bottom ml-1"
                />
                <img
                  src="/icons/cream_cat.png"
                  alt="cat icon"
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

      {/* <ul className="space-y-2 m"> */}
      <ul className="h-auto">
        {type === "personal"
          ? // 개인 가계부 데이터
            paymentData?.data?.transactions?.map((item, index) => {
              const matchedCategory = categories.find(
                (cat) => cat.name === item.category
              );
              const Icon = matchedCategory ? matchedCategory.Icon : null;

              return (
                <li
                  key={index}
                  className="flex items-center gap-2 text-lg pt-4"
                >
                  {Icon && (
                    <img
                      src={Icon}
                      alt={item.category}
                      className="w-10 h-auto"
                    />
                  )}
                  <span className="ml-[20px]">{item.category}</span>
                  <span className="relative left-30px">{item.matchedName}</span>
                  <span>{item.description}</span>
                  <span className="ml-auto">
                    {item.amount != null
                      ? `${item.amount.toLocaleString()}`
                      : "금액 없음"}
                  </span>
                </li>
              );
            })
          : !userId
          ? // 본인 공유 가계부 데이터
            paymentData?.data?.transactions?.map((item, index) => {
              const matchedCategory = categories.find(
                (cat) => cat.name === item.category
              );
              const Icon = matchedCategory ? matchedCategory.Icon : null;

              return (
                <li
                  key={index}
                  className="flex items-center gap-2 text-lg pt-4"
                >
                  {Icon && (
                    <img
                      src={Icon}
                      alt={item.category}
                      className="w-10 h-auto"
                    />
                  )}
                  <span className="ml-[20px]">{item.category}</span>
                  <span className="relative left-30px">{item.matchedName}</span>
                  <span>{item.description}</span>
                  <span className="ml-auto">
                    {item.amount != null
                      ? `${item.amount.toLocaleString()}`
                      : "금액 없음"}
                  </span>
                </li>
              );
            })
          : // 타인 공유 가계부 데이터
            paymentData?.transactions?.map((item, index) => {
              const matchedCategory = categories.find(
                (cat) => cat.name === item.category
              );
              const Icon = matchedCategory ? matchedCategory.Icon : null;

              return (
                <li
                  key={index}
                  className="flex items-center gap-2 text-lg pt-4"
                >
                  {Icon && (
                    <img
                      src={Icon}
                      alt={item.category}
                      className="w-10 h-auto"
                    />
                  )}
                  <span className="ml-[20px]">{item.category}</span>
                  <span className="relative left-30px">{item.matchedName}</span>
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
  );
}
