import { useEffect, useRef, useState } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import bankImage from "../assets/bank.png"
import Api from "../../../services/Api";
export default function Card() {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef(null);
    const [list, setList] = useState([])
    const [card, setCard] = useState({
        cardNo:"",
        cardName:"",
    });

    function handleCardSelect(card) {
        console.log(card)
        setCard({
            cardNo:card.card_no,
            cardName: card.card_name
        });
        setIsOpen(false);
    }

    // 카드 리스트 조회
    useEffect(() => {
        const fetchData = async() => {
            try {
                const response = await Api.get("api/card/refresh")
                const cardData = response.data.data.cards
                console.log(cardData)
                if (response.data.status === "success") {
                    setList(cardData)
                }               
            } catch (error) {
                console.log(error)
            }
        }
        fetchData();
    },[]);


    return(
            <div className="flex flex-col justify-center gap-5 min-w-[345px]">
                <CustomBackHeader title="카드 조회"/>
                <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                    <p className="text-left font-regular text-lg">카드 선택</p>
                    
                    {/* 드롭다운 */}
                    <div className="relative w-full" ref={dropdownRef}>
                        <div 
                            className="w-full p-2 border rounded flex items-center justify-between cursor-pointer"
                            onClick={() => setIsOpen(!isOpen)}
                        >
                            {card.cardName ? (
                                <div className="flex items-center justify-center w-full">
                                    <span>{card.cardName}</span>
                                </div>
                            ) : (
                                <span className="text-gray-400">카드를 선택해 주세요</span>
                            )}
                            <span className="ml-2">▼</span>
                        </div>
                        
                        {isOpen && (
                            <div className="absolute z-10 w-full mt-1 bg-white border rounded shadow-lg max-h-60 overflow-auto">
                                {list.map((card) => (
                                    <div 
                                        key={card.card_name}
                                        className="p-2 hover:bg-gray-100 cursor-pointer flex items-center"
                                        onClick={() => handleCardSelect(card)}
                                    >
                                        <span>{card.card_name}</span>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
                
                {/* 은행 이미지 */}
                <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4">
                    <img src={bankImage} alt="은행 이미지" />
                </div>
            
            </div>
        )
}