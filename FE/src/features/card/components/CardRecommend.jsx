import { useEffect, useState } from "react"
import CustomHeader from "../../../components/CustomHeader"
import CardCat from "../assets/CardCat.png"
import { Link } from "react-router-dom"
import Api from "../../../services/Api"
import CatFootPrint from "../assets/CatFootPrint.png"
import questionIcon from "../assets/QuestionIcon.png"
import AlertModal from "../../../components/AlertModal"

export default function CardRecommend() {
  const [cardList, setCardList] = useState([])
  const [buttonChange, setButtonChange] = useState(false)
  const [isAlertModal, setIsAlertModal] = useState(false)

  // 모달 열기
  function AlertModalOpen() {
    setIsAlertModal(true)
  }

  // 모달 닫기
  function AlertModalClose() {
    setIsAlertModal(false)
  }

  //체크카드 리스트 전환
  function checkHandler () {
    setButtonChange(true)
    const fetchData = async () => {
      try {
        const response = await Api.get("api/recommend/cards/check")
        console.log(response.data)
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
        if (response.data.status == "success") {
          setCardList(response.data.data)
          console.log(response.data.data)
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
      <div className="w-full flex flex-row justify-between m-0 bg-[#fff0ba] shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 ">
        <div className="flex flex-col justify-center text-lg">
          <p className="text-left font-semibold text-[#3d3d3d]">하나카드 3월 캐시백 이벤트</p>
          <p className="text-left text-[#3d3d3d]">이벤트 기간 3/1 ~ 3/31</p>
        </div>
        <div>
          <img src={CardCat} alt="카드 추천 고양이 이미지" />
        </div>
      </div>

      <div className="flex justify-between">
        <div className="flex">
          <button className={(!buttonChange)?"blackButton" : "whiteButton"} onClick={creditHandler}>신용카드</button>
          <button className={(buttonChange)?"blackButton" : "whiteButton"} onClick={checkHandler}>체크카드</button>
        </div>
          <img src={questionIcon} alt="물음표 아이콘" onClick={AlertModalOpen}/>
      </div>

      <AlertModal title="카드 추천은 어떻게 되는건가요?" isOpen={isAlertModal} isClose={AlertModalClose} height={350}>
        <div className="text-center">
          <p className="mb-2">가계부에 기록된 소비 패턴을 분석하여</p>
          <p className="mb-2">가장 적합한 카드를 추천해드립니다.</p>
          <p className="mb-2">주요 소비 카테고리와 금액에 따라</p>
          <p className="mb-2">최대 혜택을 받을 수 있는 카드를 선별합니다.</p>
          <p>더 많은 소비 기록이 쌓일수록</p>
          <p>추천 정확도가 높아집니다.</p>
        </div>
      </AlertModal>

      <div className="w-full flex flex-col gap-5">
        {cardList.length > 0 ? (
          cardList.map((card, index ) => (
            <Link to={`detail_card/${card.recoCardId}`} state={{ cardData: card}} key={card.recoCardId}>
              <div className="w-full flex flex-row bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 gap-10" key={index}>
                    {(index+1 === 1)
                    ? <div className="flex flex-col items-center justify-center w-[27px]">
                        <img src={CatFootPrint} alt="고양이 발자국 이미지" />
                        <p className="text-xl text-[#ff957a] font-bold">{index+1}</p>
                      </div> 
                    : <div className="flex items-center justify-center w-[27px]">
                        <p className="text-xl">{index+1}</p>
                      </div>}
                  <img src={`/${card.imagePath}`} alt="카드 이미지" style={{width:"60px", height:"100px"}} />
                  <div className="flex flex-col gap-1">
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