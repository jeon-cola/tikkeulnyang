import { useEffect, useState } from "react";
import Container from "@/components/Container";
import MonthBar from "../MonthBar";
import BudgetBar from "./BudgetBar";
import Api from "../../../../services/Api";

import EntertainmentIcon from "../../assets/category/entertainment_icon.png";
import FoodIcon from "../../assets/category/food_icon.png";
import GoodsIcon from "../../assets/category/goods_icon.png";
import HousingIcon from "../../assets/category/housing_icon.png";
import MedicalIcon from "../../assets/category/medical_icon.png";
import ShoppingIcon from "../../assets/category/shopping_icon.png";
import TransportationIcon from "../../assets/category/transportation_icon.png";
import SpenseIcon from "../../assets/category/spense_icon.png";
import EducationIcon from "../../assets/category/education_icon.png";
// import WasteBlack from "../../assets/waste_black.png";

const categories = [
  { id: 1, name: "교통/차량", Icon: TransportationIcon },
  { id: 2, name: "쇼핑/미용", Icon: ShoppingIcon },
  { id: 3, name: "교육/육아", Icon: EducationIcon },
  { id: 4, name: "주거/통신", Icon: HousingIcon },
  { id: 5, name: "문화/여가", Icon: EntertainmentIcon },
  { id: 6, name: "병원/약국", Icon: MedicalIcon },
  { id: 7, name: "식비", Icon: FoodIcon },
  { id: 8, name: "잡화", Icon: GoodsIcon },
  { id: 9, name: "결제", Icon: SpenseIcon },
];

export default function BudgetMain() {
  const [activeDate, setActiveDate] = useState(new Date());
  const [budgetData, setBudgetData] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const year = activeDate.getFullYear();
        const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");
        const reponse = await Api.get(
          `api/budget/plan?year=${year}&month=${month}`
        );
        console.log("예산 데이터 조회", reponse.data.data);
        setBudgetData(reponse.data.data);
      } catch (error) {
        console.log(error);
      }
    };
    fetchData();
  }, [activeDate]);

  return (
    <>
      <Container>
        <MonthBar
          activeDate={activeDate}
          setActiveDate={setActiveDate}
          onYearMonthChange={({ year, month }) => {
            console.log("선택된 연/월:", year, month);
          }}
        />
        <div className="relative w-full h-auto bg-white rounded-md shadow-sm px-[10px]">
          <BudgetBar />
          <div className="text-left mt-4 mb-8">
            <p className="text-sm">남은 예산(월별)</p>
            <p className="text-sm">
              {budgetData?.totals?.total_remaining_amount.toLocaleString()}원
            </p>
          </div>
          <div className="text-left mb-4">
            <p className="text-sm">예산(월별)</p>
            <p className="text-sm">
              {budgetData?.totals?.total_amount.toLocaleString()}원
            </p>
            {/* 프로그레스 바 배경 */}
            <div className="absolute left-0 bottom-0 w-full h-[24px] bg-[#F1EFEF] border border-[#DFDFDF] rounded-[70px]">
              {/* 프로그레스 바 채움 */}
              {/* <div
                className="h-full bg-[#FF957A] rounded-[70px]"
                style={{ width: `${currentProgress}%` }}
              ></div> */}
            </div>
          </div>
        </div>
      </Container>
    </>
  );
}
