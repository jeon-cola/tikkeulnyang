import { useEffect, useState } from "react"
import CustomHeader from "../../../components/CustomHeader"
import axios from "axios"
import CardCat from "../assets/CardCat.png"

export default function CardRecommend() {
  const [cardList, setCardList] = useState([])

  useEffect( () => {
    const fetchData = async()=>{
      try {
        const response = await axios.get("http://localhost:3000/recommended_cards")
        setCardList(response.data)
        console.log(response.data)
      } catch (error) {
        console.log(error)
      } 
    }
    fetchData()
  },[])

  return(
    <div className="flex flex-col gap-5">
      <CustomHeader title="티끌냥"/>

      {/* 광고 이미지 */}
      <div className="w-full flex flex-row justify-between m-0 bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 ">
        <div className="flex flex-col justify-center text-lg">
          <p className="text-left font-semibold">하나카드 3월 캐시백 이벤트</p>
          <p className="text-left">이벤트 기간 3/1 ~ 3/31</p>
        </div>
        <div>
          <img src={CardCat} alt="카드 추천 고양이 이미지" />
        </div>
      </div>

      <div className="w-full">
        {cardList.length > 0 ? (
          cardList.map((card, index ) => (
            <div className="w-full flex flex-row bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 gap-15" key={index}>
                <img src={card.image_url} alt="카드 이미지" />
                <div className="flex flex-col">
                  <p className="text-left font-semibold text-lg">{card.reco_card_name}</p>
                  <p className="text-left">{card.corp_name}</p>
                </div>
            </div>
          ))
        )
        : (
          <div className="w-full flex flex-row bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4">
            <p>추천 카드를 불러오는 중...</p>
          </div>
        )
      }
      </div>

    </div>
  )
}