import { useEffect, useState } from "react";
import MonthBar from "./components/MonthBar";
import Container from "@/components/Container";
import LedgerHeader from "./components/LedgerHeader";
import CategoryBox from "./components/CategoryBox";
import Api from "../../services/Api";

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
import WasteIcon from "./assets/waste_icon.png";
import EmptyIcon from "./assets/empty_icon.png";

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

export default function LedgerDetail() {
  const [activeDate, setActiveDate] = useState(new Date());
  const [activeCategory, setActiveCategory] = useState("all");
  const [selectedMonth, setSelectedMonth] = useState({
    totalIncome: 0,
    totalSpent: 0,
    transactionsMap: [],
  });
  const [totalIncome, setTotalIncome] = useState(0);
  const [totalSpent, setTotalSpent] = useState(0);
  const [wasteStates, setWasteStates] = useState({}); // index: true/false

  useEffect(() => {
    const fetchMonthlyData = async () => {
      try {
        const year = activeDate.getFullYear();
        const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");
        const response = await Api.get(
          `api/payment/consumption/monthly?year=${year}&month=${month}`
        );
        const data = response.data.data;
        setSelectedMonth(data);
        setTotalIncome(data.totalIncome);
        setTotalSpent(data.totalSpent);
      } catch (error) {
        console.error("월별 세부내역 조회 실패", error);
      }
    };

    fetchMonthlyData();
  }, [activeDate]);

  // 교육, 수입, 지출 추가해야하는지 확인하기
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

  const filteredTransactions =
    activeCategory === "all"
      ? selectedMonth.transactionsMap
      : selectedMonth.transactionsMap.filter((item) => {
          return item.categoryName === categoryMapping[activeCategory];
        });

  const handleWasteToggle = (index) => {
    setWasteStates((prev) => ({
      ...prev,
      [index]: !prev[index],
    }));

    // API.post 등 추가 저장 로직도 여기에 삽입 가능
  };

  return (
    <div>
      <Container>
        <LedgerHeader />
        <CategoryBox
          activeCategory={activeCategory}
          setActiveCategory={setActiveCategory}
        />
        <MonthBar
          activeDate={activeDate}
          setActiveDate={setActiveDate}
          onYearMonthChange={({ year, month }) => {
            console.log("선택된 연/월:", year, month);
          }}
        />

        {/* 수입/지출 요약 카드 */}
        <div className="w-full bg-white rounded-lg shadow-sm p-4 flex flex-col gap-3">
          <div className="flex justify-between">
            <p className="text-2xl text-gray-800">
              {activeDate.getMonth() + 1}월
            </p>
            <div className="flex gap-6 text-sm">
              <div>
                <p className="text-[#A2A2A2] text-xs">총 수입</p>
                <p className="text-[#64C9F5] font-semibold">
                  {totalIncome.toLocaleString()}
                </p>
              </div>
              <div>
                <p className="text-[#A2A2A2] text-xs">총 지출</p>
                <p className="text-[#FF957A] font-semibold">
                  {totalSpent.toLocaleString()}
                </p>
              </div>
            </div>
          </div>

          {/* 상세 내역 */}
          <ul>
            {filteredTransactions.map((item, index) => {
              const matchedCategory = categories.find(
                (cat) => cat.name === item.categoryName
              );
              const Icon = matchedCategory?.Icon;

              const dateObj = new Date(item.date);
              const formattedDate = `${
                dateObj.getMonth() + 1
              }/${dateObj.getDate()}`;

              const isWaste = wasteStates[index] || false;

              return (
                <li
                  key={index}
                  className="flex items-center justify-between text-sm mb-2"
                >
                  {/* 왼쪽: 아이콘 + 날짜 + 상호명 */}
                  <div className="flex items-center gap-2">
                    {Icon && (
                      <img
                        src={Icon}
                        alt={item.category}
                        className="w-6 h-6 mr-[5px]"
                      />
                    )}
                    <span>{formattedDate}</span>
                    <span className="ml-8">{item.merchantName || "-"}</span>
                  </div>

                  {/* 오른쪽: 금액 + 낭비 아이콘 */}
                  <div className="flex items-center gap-4">
                    <div className="min-w-[100px] font-semibold text-gray-800">
                      {item.amount.toLocaleString()}
                    </div>
                    <img
                      src={isWaste ? WasteIcon : EmptyIcon}
                      alt="낭비 체크"
                      onClick={() => handleWasteToggle(index)}
                      className={`w-6 h-6 cursor-pointer transition-all duration-300 ${
                        isWaste ? "animate-pop" : ""
                      }`}
                    />
                  </div>
                </li>
              );
            })}
          </ul>
        </div>
      </Container>

      {/* 애니메이션 스타일 */}
      <style jsx>{`
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
      `}</style>
    </div>
  );
}
