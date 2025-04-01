import { useEffect, useState } from "react";
import Api from "../../../services/Api";
import EntertainmentIcon from "../assets/category/entertainment_icon.png";
import FoodIcon from "../assets/category/food_icon.png";
import GoodsIcon from "../assets/category/goods_icon.png";
import HousingIcon from "../assets/category/housing_icon.png";
import MedicalIcon from "../assets/category/medical_icon.png";
import ShoppingIcon from "../assets/category/shopping_icon.png";
import TransportationIcon from "../assets/category/transportation_icon.png";
import IncomeIcon from "../assets/category/income_icon.png";
import SpenseIcon from "../assets/category/spense_icon.png";

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

const formatKoreanDate = (dateStr) => {
  const date = new Date(dateStr + "T00:00:00+09:00"); // 타임존 보정
  const day = date.getDate(); // 날짜
  const weekday = date.toLocaleDateString("ko-KR", { weekday: "long" });
  return `${day}일 ${weekday}`;
};
console.log("PaymentDetails에서 시간조회:", new Date());
export default function PaymentDetails({ date }) {
  const [paymentData, setPaymentData] = useState(null);

  useEffect(() => {
    const fetchedPaymentData = async () => {
      try {
        const response = await Api.get(`api/payment/consumption/daily/${date}`);
        console.log("일별 세부내역 데이터:", response.data.data);
        setPaymentData(response.data.data); // 여기서 저장
      } catch (error) {
        console.error("일별 세부내역조회 실패", error);
      }
    };

    if (date) {
      fetchedPaymentData();
    }
  }, [date]); //data가 바뀔 때마다 다시 요청

  if (!paymentData) return <div>로딩 중...</div>;

  return (
    <div className="bg-white w-full p-[10px] text-black">
      <p className="flex flex-start pb-[10px]">
        {formatKoreanDate(paymentData.date)}
      </p>

      <ul className="space-y-2">
        {paymentData.transactions.map((item, index) => {
          const matchedCategory = categories.find(
            (cat) => cat.id === item.category
          );
          const Icon = matchedCategory ? matchedCategory.Icon : null;

          return (
            <li key={index} className="flex items-center gap-2 text-sm">
              {Icon && (
                <img src={Icon} alt={item.category} className="w-8 h-auto" />
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
