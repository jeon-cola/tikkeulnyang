import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom"
import Api from "../../../services/Api";
import CustomBackHeader from "../../../components/CustomBackHeader";

export default function DetailCard() {
  const location = useLocation();
  const cardData = location.state?.cardData
  const [cardList, setCardList] = useState({
    "cardName":"",
    "cardType":"",
    "corpName":"",
    "imagePath":"",
    "benefits":[]
  })

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await Api.get(`api/recommend/cards/${cardData.recoCardId}`)
        console.log(response.data)
        const card = response.data
        setCardList({
          "cardName":card.cardName,
          "cardType":card.cardType,
          "corpName":card.corpName,
          "imagePath":card.imagePath,
          "benefits":card.benefits
        })
      } catch (error) {
        console.log(error)
      }
    }
    fetchData()
  },[])

  return(
    <div className="w-full flex flex-col gap-5">
      <CustomBackHeader title={cardList.cardName}/>
      <div className="w-full flex justify-center">
        <img src={`/${cardList.imagePath}`} alt="카드 이미지" style={{width:"225px", height:"360px"}} />
      </div>
      <p className="font-semibold text-2xl">{cardList.cardName}</p>
      <p className="text-xl">{cardList.corpName}</p>
      <div className="w-full flex flex-col gap-3">
        <div className="w-full flex flex-col justify-center">
         {(Array.isArray(cardList.benefits) && cardList.benefits.length > 0)?cardList.benefits.map((benefit)=>
         
          <p className="text-left w-1/3">{benefit.category}</p> 
        ):""}
        </div>
      </div>
    </div>
  )
}