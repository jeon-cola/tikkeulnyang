import { useEffect, useState } from "react"
import CustomHeader from "../../../components/CustomHeader"
import CardCat from "../assets/CardCat.png"
import { Link } from "react-router-dom"
import Api from "../../../services/Api"
import CatFootPrint from "../assets/CatFootPrint.png"

export default function CardRecommend() {
  const [cardList, setCardList] = useState([])
  const [buttonChange, setButtonChange] = useState(false)

  //체크카드 리스트 전환
  function checkHandler () {
    setButtonChange(true)
    const fetchData = async () => {
      try {
        const response = await Api.get("api/recommend/cards/check")
        if (response.data.status == "success") {
          setCardList(response.data.data)
        }
      } catch (error) {
        console.log(error)
      }
    }
    fetchData()
  }

  //신용카드 리스트 전환
  function creditHandler() {
    setButtonChange(false)
    const fetchData = async()=>{
      try {
        const response = await Api.get("api/recommend/cards/credit")
        if (response.data.status == "success") {
          setCardList(response.data.data)
        }
      } catch (error) {
        console.log(error)
      } 
    }
    fetchData()
  }

  // 초기 신용카드 렌더링
  useEffect( () => {
    const fetchData = async()=>{
      try {
        const response = await Api.get("api/recommend/cards/credit")
        console.log(response.data)
        if (response.data.status == "success") {
          setCardList(response.data.data)
        }
      } catch (error) {
        console.log(error)
      } 
    }
    fetchData()
  },[])

  return(
    <div className="flex flex-col gap-5 mt-[50px]">
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

      <div className="flex">
        <button className={(!buttonChange)?"blackButton" : "whiteButton"} onClick={creditHandler}>신용카드</button>
        <button className={(buttonChange)?"blackButton" : "whiteButton"} onClick={checkHandler}>체크카드</button>
      </div>

      <div className="w-full flex flex-col gap-5">
        {cardList.length > 0 ? (
          cardList.map((card, index ) => (
            <Link to={`detail_card/${card.recoCardId}`} state={{ cardData: card}} key={card.recoCardId}>
              <div className="w-full flex flex-row bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 gap-15" key={index}>
                    {(index+1 === 1)
                    ? <div className="flex flex-col items-center justify-center">
                        <img src={CatFootPrint} alt="고양이 발자국 이미지" />
                        <p className="text-xl text-[#ff957a] font-bold">{index+1}</p>
                      </div> 
                    : <div className="flex items-center justify-center">
                        <p className="text-xl">{index+1}</p>
                      </div>}
                  <img src={card.imagePath} alt="카드 이미지" style={{width:"60px", height:"100px"}} />
                  <div className="flex flex-col">
                    <p className="text-left font-semibold text-lg">{card.recoCardName}</p>
                    <p className="text-left">{card.corpName}</p>
                  </div>
              </div>
            </Link>
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