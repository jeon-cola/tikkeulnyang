import { useEffect, useState } from "react";
import MonthBar from "../MonthBar";
import BudgetBar from "./BudgetBar";
import Api from "../../../../services/Api";
import CustomBackHeader from "@/components/CustomBackHeader";
import CategoryList from "../CategoryList";
import WasteBlackIcon from "../../assets/waste_black.png";
import Buz from "../../assets/buz-124000.png";
import Ring from "../../assets/ring_499400.png";
import Tab from "../../assets/tab_9_870000.png";
import Watch from "../../assets/watch_7_214000.png";

const categories = CategoryList();

const getWasteImage = (amount) => {
  if (amount >= 870000) {
    return { image: Tab, name: "갤럭시 탭 9" };
  } else if (amount >= 499400) {
    return { image: Ring, name: "갤럭시 링" };
  } else if (amount >= 214000) {
    return { image: Watch, name: "갤럭시 워치" };
  } else if (amount >= 124000) {
    return { image: Buz, name: "갤럭시 버즈" };
  } else {
    return null;
  }
};

const getObjectPostfix = (word) => {
  const lastChar = word[word.length - 1];
  const code = lastChar.charCodeAt(0);
  const hasFinalConsonant = (code - 44032) % 28 !== 0;
  return hasFinalConsonant ? "을" : "를";
};

const getDaysLeftInMonth = (date) => {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const today = date.getDate();
  const lastDay = new Date(year, month, 0).getDate();
  return lastDay - today + 1;
};

export default function BudgetMain() {
  const [activeDate, setActiveDate] = useState(new Date());
  const [budgetData, setBudgetData] = useState(null);
  const [totalWaste, setTotalWaste] = useState(null);

  const year = activeDate.getFullYear();
  const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");

  useEffect(() => {
    const fetchWasteData = async () => {
      try {
        const res = await Api.get(
          `api/budget/waste/money?year=${year}&month=${month}`
        );
        console.log("낭비 데이터 조회", res.data);

        if (res.data.status === "success") {
          const wasteAmount = res.data.data.total_waste_amount;
          console.log("total_waste_amount 값:", wasteAmount);
          setTotalWaste(wasteAmount);
        }
      } catch (err) {
        console.error(err);
      }
    };
    fetchWasteData();
  }, [year, month]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await Api.get(
          `api/budget/categories?year=${year}&month=${month}`
        );
        console.log("예산 데이터 조회", response.data.data);
        setBudgetData(response.data.data);
      } catch (error) {
        console.log(error);
      }
    };
    fetchData();
  }, [year, month]);

  const total = budgetData?.totals?.total_amount || 0;
  const remaining = budgetData?.totals?.total_remaining_amount || 0;
  const spending = budgetData?.totals?.total_spending_amount || 0;
  const isExceed = budgetData?.totals?.total_is_exceed || false;
  const daysLeft = getDaysLeftInMonth(activeDate);
  const dailyAvailable = daysLeft > 0 ? Math.floor(remaining / daysLeft) : 0;
  const percent = Math.min((remaining / total) * 100, 100);
  const exceedPercent = Math.abs((remaining / total) * 100);

  return (
    <>
      <CustomBackHeader title="예산보기" />
      <BudgetBar />
      <div className="mt-4">
        <div className="w-full bg-white rounded-lg shadow-sm mb-4">
          <MonthBar
            activeDate={activeDate}
            setActiveDate={setActiveDate}
            onYearMonthChange={({ year, month }) => {
              console.log("선택된 연/월:", year, month);
            }}
          />
        </div>

        {/* 예산 정보 */}
        <div className="relative w-full h-auto bg-white rounded-md shadow-sm px-[10px] py-2">
          <div className="flex justify-between items-center mt-4 mb-4">
            <div className="text-left mb-2">
              <p className="text-xs text-gray-500">남은 예산(월별)</p>
              <p className="text-xl font-semibold text-black">
                {remaining.toLocaleString()}원
              </p>
            </div>
            <div className="text-right">
              <p className="text-xs text-gray-500">남은 일일 예산</p>
              <p className="text-base font-semibold text-black">
                {dailyAvailable.toLocaleString()}원 /일
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="text-left min-w-[100px]">
              <p className="text-xs text-gray-500">예산(월별)</p>
              <p className="text-xl font-semibold text-black">
                {total.toLocaleString()}원
              </p>
            </div>

            <div className="relative w-full h-[24px] bg-[#F1EFEF] border border-[#DFDFDF] rounded-[70px] mt-2 overflow-hidden">
              {isExceed ? (
                <div
                  className="h-full bg-[#FF957A] rounded-[70px] text-white text-[12px] text-center"
                  style={{ width: `${Math.min(exceedPercent, 100)}%` }}
                >
                  {exceedPercent.toFixed(0)}%
                </div>
              ) : (
                <div
                  className="h-full bg-[#AAE1FE] rounded-[70px] text-white text-[12px] text-center"
                  style={{ width: `${percent}%` }}
                >
                  {percent.toFixed(0)}%
                </div>
              )}
            </div>
          </div>
        </div>

        {/* 카테고리별 막대 차트 */}
        <div className="relative w-full h-auto bg-white rounded-md shadow-sm mt-4 space-y-4">
          {budgetData?.categories?.map((item) => {
            const category = categories.find((c) => c.id === item.categoryId);
            const used = item.spendingAmount || 0;
            const budget = item.amount || 0;
            const remaining = item.remainingAmount;
            const exceed = item.isExceed === 1;
            const percent =
              budget > 0 ? Math.min((used / budget) * 100, 100) : 0;

            return (
              <div
                key={item.categoryId}
                className="flex items-center gap-3 px-3 py-2"
              >
                <div className="flex items-center gap-2 min-w-[140px]">
                  {category?.Icon && (
                    <img
                      src={category.Icon}
                      alt={item.categoryName}
                      className="w-12 h-auto object-contain"
                    />
                  )}
                  <div className="text-left">
                    <p>{category?.name || "기타"}</p>
                    <p>{budget.toLocaleString()}원</p>
                  </div>
                </div>
                <div className="flex-1">
                  <div className="flex justify-between text-sm font-medium mb-1">
                    <span>{item.name}</span>
                  </div>
                  <div className="relative w-full h-[25px] bg-[#ECECEC] rounded-full overflow-hidden">
                    <div
                      className={`h-full ${
                        exceed ? "bg-[#FF957A]" : "bg-[#AAE1FE]"
                      } rounded-full`}
                      style={{ width: `${percent}%` }}
                    >
                      <span className="text-right!">{percent.toFixed(0)}%</span>
                    </div>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-right text-[12px] text-gray-500 mt-1">
                      {used.toLocaleString()}
                    </span>
                    <span className="text-right text-[12px] text-gray-500 mt-1">
                      {remaining.toLocaleString()}
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* 낭비항목 */}
        <div className="relative w-full h-auto bg-white rounded-md shadow-sm my-2 pb-8">
          <div className="flex flex-col items-center justify-center text-center font-semibold">
            {totalWaste >= 124000 ? (
              <>
                {(() => {
                  const result = getWasteImage(totalWaste);
                  return (
                    result && (
                      <>
                        <p className="pt-5">
                          낭비금액{" "}
                          {totalWaste !== null
                            ? totalWaste.toLocaleString()
                            : "0"}
                          원
                        </p>
                        <img
                          src={result.image}
                          alt="낭비 물건 이미지"
                          className="w-[180px] h-auto py-4"
                        />
                        <p>
                          {result.name}
                          {getObjectPostfix(result.name)} 살 수 있는
                        </p>
                        <p>금액을 낭비했어요</p>
                      </>
                    )
                  );
                })()}
              </>
            ) : (
              <div className="flex flex-col items-center justify-center text-center font-semibold pt-7">
                <img
                  src={WasteBlackIcon}
                  alt="기본 낭비 아이콘"
                  className="w-10 h-auto"
                />

                <p className="pt-3">이번 달 낭비금액은</p>
                <p>
                  {totalWaste !== null ? totalWaste.toLocaleString() : "0"}
                  원이네요
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
