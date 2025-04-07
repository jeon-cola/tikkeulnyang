import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom"
import Api from "../../../services/Api";
import CustomBackHeader from "../../../components/CustomBackHeader";
import getIcon from "../assets/getIcon";
import getCardIcon from "../assets/getCardIcon"
import getCardColor from "../assets/getCardColor";
import '@fortawesome/fontawesome-free/css/all.min.css';
import IsLoading from "../../../components/IsLoading"


export default function DetailCard() {
  const [isLoading, setIsLoading] = useState(false)
  const location = useLocation();
  const cardData = location.state?.cardData
  const [cardList, setCardList] = useState({
    "cardName": "",
    "cardType": "",
    "corpName": "",
    "imagePath": "",
    "benefits": []
  })

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await Api.get(`api/recommend/cards/${cardData.recoCardId}`)
        console.log(response.data)
        const card = response.data
        setCardList({
          "cardName": card.cardName,
          "cardType": card.cardType,
          "corpName": card.corpName,
          "imagePath": card.imagePath,
          "benefits": card.benefits
        })
        setIsLoading(true)
      } catch (error) {
        console.log(error)
      }
    }
    fetchData()
  }, [cardData])

  return (
    <>
    {isLoading?
    (<div className="w-full flex flex-col gap-5 mt-10 mb-2">
      
      <CustomBackHeader title={cardList.cardName} />
      
      <div className="w-full flex justify-center mt-5">
      <img src={`/${cardList.imagePath}`} alt="카드 이미지" style={{ width: "225px", height: "360px" }} />
      </div>
      
      <p className="font-semibold text-2xl">{cardList.cardName}</p>
      
      <p className="text-xl">{cardList.corpName}</p>
      
      <div className="w-full overflow-x-auto">
      <div className="flex gap-3 px-3 py-2 w-max">
      {cardList.benefits.map((benefit, index) => (
        <div
        key={index}
        className="flex items-center gap-2 px-4 py-2 bg-white rounded-full shadow-sm border border-gray-200 whitespace-nowrap"
        >
              <i
                className={getCardIcon(benefit.category)}
                style={{ fontSize: "20px", color: getCardColor(benefit.category) }}
                ></i>
              <span className="text-sm font-medium">{benefit.category}</span>
            </div>
          ))}
          </div>
          </div>
          
          
          <div className="w-full flex flex-col">
          
          <p className="text-left text-xl font-bold mb-2">주요혜택</p>
          
          <div className="w-full flex flex-col gap-4">
          {cardList.benefits.map((benefit, index) => (
            <div
            key={index}
            className="flex items-center gap-4 w-full p-4 rounded-2xl shadow-md bg-white"
            >
              <i
                className={getCardIcon(benefit.category)}
                style={{
                  fontSize: "28px",
                  color: getCardColor(benefit.category),
                  minWidth: "32px",
                }}
                ></i>
              <div className="flex flex-col text-left">
                <span className="font-semibold text-base text-gray-800">
                  {benefit.category}
                </span>
                <span className="text-sm text-gray-600 mt-1">
                  {benefit.description}
                </span>
              </div>
            </div>
          ))}
          </div>
          
          </div>
          
          </div>)
        :(<IsLoading/>)}
        </>
        )
      }