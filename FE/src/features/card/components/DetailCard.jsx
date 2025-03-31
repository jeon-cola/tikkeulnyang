import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom"
import Api from "../../../services/Api";
import CustomBackHeader from "../../../components/CustomBackHeader";
import getIcon from "../assets/getIcon";

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
  },[cardData])

  return(
    <div className="w-full flex flex-col gap-5 mt-10 mb-2">

      <CustomBackHeader title={cardList.cardName}/>

      <div className="w-full flex justify-center mt-5">
        <img src={`/${cardList.imagePath}`} alt="카드 이미지" style={{width:"225px", height:"360px"}} />
      </div>

      <p className="font-semibold text-2xl">{cardList.cardName}</p>

      <p className="text-xl">{cardList.corpName}</p>

      <div className="w-full flex flex-col justify-center items-center">
        <div className="w-full flex flex-col justify-center items-center gap-2">
          {(Array.isArray(cardList.benefits) && cardList.benefits.length > 0) ? cardList.benefits.map((benefit, index) =>
            <div className="flex gap-5 justify-center items-center w-full" key={index}>
              <img src={getIcon(benefit.budgetCategory)} alt={`${benefit.budgetCategory} 아이콘`} style={{height:"30px", width:"30px"}}/>
              <p className="text-left w-1/3 text-xl font-bold">{benefit.category}</p> 
            </div>
          ) : ""}
        </div>
      </div>

      <div className="w-full flex flex-col">

          <p className="text-left text-xl font-bold">주요혜택</p>

        <div className="w-full flex flex-col justify-center items-center gap-4">
          {(Array.isArray(cardList.benefits) && cardList.benefits.length > 0) ? cardList.benefits.map((benefit, index) =>
            <div className="flex gap-5 justify-center items-center w-full" key={index}>
              <li className="text-left w-1/3">{benefit.category}</li> 
              <p className="text-left w-1/2">{benefit.description}</p> 
            </div>
          ) : ""}
        </div>
      </div>
      
    </div>
  )
}