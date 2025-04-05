import { useEffect, useState } from "react";
import CustomBackHeader from "../../../components/CustomBackHeader";
import Api from "../../../services/Api";

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
      
      <div className="w-full relative h-[40vh] overflow-hidden">
        <div className="card-stack-container w-full h-full flex items-center justify-center">
          {list.map((cardItem, index) => (
            <div 
              key={cardItem.card_name} 
              onClick={() => handleCardSelect(cardItem)} 
              className={`card-item absolute w-13/14 transform transition-all duration-300 ease-in-out`}
              style={{ 
                zIndex: selectedCardNo === cardItem.card_no ? 100 : list.length - index,
                top: selectedCardNo === cardItem.card_no ? '43%' : `${Math.min(45 + (index * 5), 80)}%`,
                left: '50%',
                transform: `translateX(-50%) translateY(-50%) ${
                  selectedCardNo === cardItem.card_no ? 'scale(1.05) translateY(-5px)' : `rotate(${-5 + (index * 2)}deg)`
                }`,
                boxShadow: '0 4px 8px rgba(0,0,0,0.1)',
                cursor: 'pointer'
              }}
            >   
              <img 
                src={`/${cardItem.imagePath}`} 
                alt={`${cardItem.card_name} 이미지`} 
                className="w-full"
                style={{
                  transition: 'transform 0.3s ease'
                }}
              />
              
            </div>
          ))}
        </div>
      </div>
      
      {/* 선택된 카드 정보 표시 영역 */}
      {selectedCardNo && (
        <div className="mt-8 p-6 bg-gradient-to-b from-[#FFECE6] to-white rounded-2xl shadow-2xl w-full max-w-md border border-[#FFD5C7] transition duration-300 hover:scale-105 hover:shadow-2xl gap-2">
        <h3 className="font-bold text-2xl text-[#FF7043] text-center tracking-wide mb-2">
          {card.cardName}
        </h3>
        {card.imageUrl && (
          <img
            src={card.imageUrl}
            alt={`${card.cardName} 이미지`}
            className="rounded-lg shadow-md mb-4 w-full object-cover h-44 border border-gray-200"
          />
        )}
        <h4 className="text-md font-semibold text-gray-800 mt-2 mb-2 text-left">
          💳 카드 혜택
        </h4>
        {card.benefits && card.benefits.length > 0 ? (
          <ul className="space-y-2 text-gray-700 text-sm ml-2">
            {card.benefits.map((benefit, index) => (
                <li key={index} className="flex items-start gap-1.5 leading-tight">
                  <span className="text-[#FF7043] mt-0.5">🍑</span>
                  <span className="break-words">{benefit}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-red-500 text-sm">혜택정보를 가져오지 못했습니다...</p>
        )}
      </div>
      
      )}
    </div>
  );
}