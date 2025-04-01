import { useEffect, useState } from "react";
import CustomBackHeader from "../../../components/CustomBackHeader";
import Api from "../../../services/Api";
import { div } from "framer-motion/client";

export default function Card() {
  const [list, setList] = useState([]);
  const [selectedCardNo, setSelectedCardNo] = useState(null);
  const [card, setCard] = useState({
    cardNo: "",
    cardName: "",
    cardImage: "",
    benefits:[]
  });

  function handleCardSelect(card) {
    console.log(card);
    // 이미 선택된 카드를 다시 클릭하면 선택 해제
    if (selectedCardNo === card.card_no) {
      setSelectedCardNo(null);
    } else {
      setSelectedCardNo(card.card_no);
    }
    
    setCard({
      cardNo: card.card_no,
      cardName: card.card_name,
      cardImage: card.imagePath,
      benefits:card.benefits
    });
  }

  // 카드 리스트 조회
  useEffect(() => {
    const fetchData = async() => {
      try {
        const response = await Api.get("api/card");
        const cardData = response.data.data.cards;
        console.log(cardData);
        if (response.data.status === "success") {
          setList(cardData);
        }               
      } catch (error) {
        console.log(error);
      }
    };
    fetchData();
  }, []);

  return(
    <div className="flex flex-col justify-center items-center">
      <CustomBackHeader title="카드 조회"/>
      
      <div className="w-full relative h-[60vh] overflow-hidden">
        <div className="card-stack-container w-full h-full flex items-center justify-center">
          {list.map((cardItem, index) => (
            <div 
              key={cardItem.card_name} 
              onClick={() => handleCardSelect(cardItem)} 
              className={`card-item absolute w-5/6 max-w-sm transform transition-all duration-300 ease-in-out`}
              style={{ 
                aspectRatio: '9/10',
                zIndex: selectedCardNo === cardItem.card_no ? 100 : list.length - index,
                top: selectedCardNo === cardItem.card_no ? '20%' : `${20 + (index * 5)}%`,
                left: '50%',
                transform: `translateX(-50%) translateY(-50%) ${
                  selectedCardNo === cardItem.card_no ? 'scale(1.05)' : `rotate(${-5 + (index * 2)}deg)`
                }`,
                boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                cursor: 'pointer'
              }}
            >   
              <img 
                src={`/${cardItem.imagePath}`} 
                alt={`${cardItem.card_name} 이미지`} 
                className="w-full h-full object-contain"
                style={{
                  transition: 'transform 0.3s ease'
                }}
              />
              
              {/* 선택된 카드에만 표시되는 정보 */}
              {selectedCardNo === cardItem.card_no && (
                <div className="absolute bottom-20 left-0 right-0 bg-opacity-50 bg-opacity-70 text-white p-3 text-center rounded-[10px]">
                  <h3 className="text-lg font-semibold">{cardItem.card_name}</h3>
                  <p className="text-sm opacity-80">카드번호: {cardItem.card_no}</p>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
      
      {/* 선택된 카드 정보 표시 영역 */}
      {selectedCardNo && (
        <div className="mt-6 p-4 bg-gray-100 rounded-lg w-4/5 max-w-sm">
          <h3 className="font-semibold text-lg">{card.cardName}</h3>
          {card.benefits && card.benefits.length > 0 ?
            card.benefits.map((benefit)=>(
                <div>
                    <p>{benefit}</p>
                </div>
            )):<p>혜택정보를 가져오지 못했습니다...</p>  
        }
        </div>
      )}
    </div>
  );
}