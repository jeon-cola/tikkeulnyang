import { useEffect, useRef, useState } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import bankImage from "../assets/bank.png"
import axios from "axios";
export default function Card() {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef(null);
    const [list, setList] = useState([])
    const [card, setCard] = useState({
        cardNo:"",
        CardName:"",
    });


    useEffect(() => {
        const fetchData = async() => {
            try {
                // const response = await Api.post("api/card/refresh")
                // const response = await axios.get("http://localhost:8080/api/card/refresh",{
                //     withCredentials:true
                // })
                if (response.data.status === "success") {
                    const cardData = response.data.data.cards
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
                <CustomBackHeader title="카드 등록"/>
                <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                    <p className="text-left font-regular text-lg">카드 선택</p>
                    
                    {/* 드롭다운 */}
                    <div className="relative w-full" ref={dropdownRef}>
                        <div 
                            className="w-full p-2 border rounded flex items-center justify-between cursor-pointer"
                            onClick={() => setIsOpen(!isOpen)}
                        >
                            {card.bank ? (
                                <div className="flex items-center justify-center w-full">
                                    <span>{card.cardNo}</span>
                                </div>
                            ) : (
                                <span className="text-gray-400">대표 계좌를 선택해 주세요</span>
                            )}
                            <span className="ml-2">▼</span>
                        </div>
                        
                        {isOpen && (
                            <div className="absolute z-10 w-full mt-1 bg-white border rounded shadow-lg max-h-60 overflow-auto">
                                {list.map((card) => (
                                    <div 
                                        key={card.CardName}
                                        className="p-2 hover:bg-gray-100 cursor-pointer flex items-center"
                                        onClick={() => handleBankSelect(card)}
                                    >
                                        <span>{card.CardName}</span>
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
                
                {/* 저장 버튼 */}
                <div className="w-full flex flex-col items-center">
                    <button className="bg-blue-500 text-white px-4 py-2 rounded" >저장하기</button>
                </div>
            </div>
        )
}