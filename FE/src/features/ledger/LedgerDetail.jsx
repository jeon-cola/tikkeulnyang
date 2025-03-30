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

const categories = [
  { id: "식비", Icon: FoodIcon },
  { id: "주거/통신", Icon: HousingIcon },
  { id: "잡화", Icon: GoodsIcon },
  { id: "병원/약국", Icon: MedicalIcon },
  { id: "쇼핑/미용", Icon: ShoppingIcon },
  { id: "교통/차량", Icon: TransportationIcon },
  { id: "문화/여가", Icon: EntertainmentIcon },
  { id: "수입", Icon: IncomeIcon },
  { id: "결제", Icon: SpenseIcon },
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
            // 추가적인 API 호출이나 상태 업데이트 가능
          }}
        />

        {/* 수입/지출 요약 카드 */}
        <div className="w-full bg-white rounded-lg shadow-sm p-4 flex flex-col gap-3">
          {/* 좌) month, 우) totalIncome과 totalSpent */}
          <div className="flex justify-between">
            <p className="text-2xl flex flex-start text-gray-800">
              {activeDate.getMonth() + 1}월
            </p>
            <div className="flex gap-6 text-sm text-left">
              <div className="text-left">
                <p className="text-[#A2A2A2] text-xs">총 수입</p>
                <p className="text-[#FF957A] font-semibold">
                  {totalIncome.toLocaleString()}
                </p>
              </div>
              <div className="text-left">
                <p className="text-[#A2A2A2] text-xs">총 지출</p>
                <p className="text-[#64C9F5] font-semibold">
                  {totalSpent.toLocaleString()}
                </p>
              </div>
            </div>
          </div>

          {/* 상세 내역 */}
          <div>
            <ul>
              {filteredTransactions.map((item, index) => {
                const matchedCategory = categories.find(
                  (cat) => cat.id === item.categoryName
                );
                const Icon = matchedCategory?.Icon;

                const dateObj = new Date(item.date);
                const formattedDate = `${
                  dateObj.getMonth() + 1
                }/${dateObj.getDate()}`;

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

                    {/* 오른쪽: 금액 */}
                    <div className="text-right min-w-[100px] font-semibold text-gray-800">
                      {item.amount.toLocaleString()}
                    </div>
                  </li>
                );
              })}
            </ul>
          </div>
        </div>
      </Container>
    </div>
  );
}
