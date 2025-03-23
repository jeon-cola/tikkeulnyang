import { useLocation } from "react-router-dom"

export default function DetailCard() {
  const location = useLocation();
  const cardData = location.state?.cardData

  return(
    <div className="w-full flex flex-col gap-5">
      <img src={cardData.image_url} alt="카드 이미지" />
      <p className="font-semibold text-2xl">{cardData.reco_card_name}</p>
      <p className="text-xl">{cardData.corp_name}</p>
      <div className="w-full flex flex-col gap-3">
        {cardData.benefits.map((benefits)=>(
          <div className="w-full">
            <p className="text-left">{benefits.description}</p>
          </div>
        ))}
      </div>
    </div>
  )
}