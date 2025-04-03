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
import EducationIcon from "../assets/category/education_icon.png";
import Comment from "./Comment";

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

const formatKoreanDate = (dateStr) => {
  const date = new Date(dateStr);
  const day = date.getDate(); // 날짜 (11)
  const weekday = date.toLocaleDateString("ko-KR", { weekday: "long" }); // 화요일
  return `${day}일 ${weekday}`;
};

export default function PaymentDetails({ date, type,userId = null }) {
  const [paymentData, setPaymentData] = useState(null);
  const [isOpen, setIsOPen] =useState(false)

  //모달 닫기
  function onCloseModalHandler() {
    setIsOPen(false)
  }

  useEffect(() => {
    const fetchedPaymentData = async () => {
      try {
        // 상대방 아이디가 있고 공유일때만
        if (type === "share" && userId) {
          if (userId){
            const response = await Api.get(`api/share/ledger/user/${userId}/daily/${date}`)
            if (response.data.status === "success") {
              setPaymentData(response.data.data)
            } 
          } 
        } else {
          const response = await Api.get(`api/payment/consumption/daily/${date}`);
          console.log("일별 세부내역 데이터:", response.data.data);
          setPaymentData(response.data); 
        }
      } catch (error) {
        console.error("일별 세부내역조회 실패", error);
      }
    };

    if (date) {
      fetchedPaymentData();
    }
  }, [date]); //data가 바뀔 때마다 다시 요청

  if (!paymentData) return <div>소비내역이 없습니다</div>;


  return (
    <div className="bg-white w-full p-[10px] text-black">
      {type === "personal" ? <p className="flex flex-start pb-[10px]">{formatKoreanDate(paymentData?.data?.date)}</p>
      : <div className="flex justify-between">
          <p className="flex flex-start pb-[10px]">
            {formatKoreanDate(!userId ? paymentData?.data?.date : paymentData?.date)}
          </p>
          <div onClick={()=> setIsOPen(true)}>
            <Comment title="댓글" isOpen={isOpen} onClose={onCloseModalHandler} userId={userId} date={!userId ? paymentData?.data?.date : paymentData?.date}/> 
          </div>
        </div>}
      
  
      <ul className="space-y-2">
        {type === "personal" ? (
          // 개인 가계부 데이터
          paymentData?.data?.transactions?.map((item, index) => {
            const matchedCategory = categories.find(
              (cat) => cat.name === item.category
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
          })
        ) : !userId ? (
          // 본인 공유 가계부 데이터
          paymentData?.data?.transactions?.map((item, index) => {
            const matchedCategory = categories.find(
              (cat) => cat.name === item.category
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
          })
        ) : (
          // 타인 공유 가계부 데이터
          paymentData?.transactions?.map((item, index) => {
            const matchedCategory = categories.find(
              (cat) => cat.name === item.category
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
          })
        )}
      </ul>
    </div>
  );
}