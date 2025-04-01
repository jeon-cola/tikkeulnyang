import { useEffect, useRef, useState } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import bankImage from "../assets/bank.png"
import Api from "../../../services/Api";
export default function Card() {
    const [list, setList] = useState([])
    const [card, setCard] = useState({
        cardNo:"",
        cardName:"",
        cardImage:""
    });

    function handleCardSelect(card) {
        console.log(card)
        setCard({
            cardNo:card.card_no,
            cardName: card.card_name,
            cardImage: card.imagePath
        });
    }

    // 카드 리스트 조회
    useEffect(() => {
        const fetchData = async() => {
            try {
                const response = await Api.get("api/card")
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
        <div className="flex flex-col justify-center gap-5">
            <CustomBackHeader title="카드 조회"/>
            {list && (
                <div className="w-full">
                    {list.map((card) => (
                        <div 
                            key={card.card_name} 
                            onClick={() => handleCardSelect(card)} 
                            className="w-full flex justify-center my-4 relative"
                            style={{ aspectRatio: '1/1' }}
                        >   
                            <img 
                                src={`/${card.imagePath}`} 
                                alt={`${card.card_name} 이미지`} 
                                className="rotate-270 w-full object-contain"
                            />
                        </div>
                    ))}
                </div>
            )}
        </div>
        )
}