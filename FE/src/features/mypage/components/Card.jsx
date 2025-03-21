import { useState } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import bankImage from "../assets/bank.png"
export default function Card() {
    const [card, setCard] = useState({
        cardName:"",
        name:"",
        cardNum:""
    });

    function inputHandler(e) {
        const {name,value} = e.target;
        console.log(name,value)
        setCard({
            ...card,
            [name]:value
        });
    };

    return(
        <div className="flex flex-col justify-center gap-5 min-w-[345px]">
            <CustomBackHeader title="등록 계좌 설정"/>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                <p className="text-left font-regular text-lg">카드 선택</p>
                <select id="" className="w-full" name="cardName" value={card.cardName} onChange={inputHandler}>
                    <option value="" disabled>카드를 선택해 주세요</option>
                    <option value="국민은행 나라 사랑카드">국민은행 나라사랑 카드</option>
                </select>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                <p className="text-left font-regular text-lg">카드 번호</p>
                <input type="text" placeholder="계좌 번호를 입력해 주세요" className="w-full" name="cardNum" value={card.cardNum} onChange={inputHandler}/>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-2">
                <p className="text-left font-regular text-lg">예금주</p>
                <input type="text" placeholder="예금주 성함을 입력해 주세요" className="w-full" name="name" value={card.name} onChange={inputHandler}/>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0,05)] rounded-[6px] p-4">
                <img src={bankImage} alt="은행 이미지" />
            </div>
            <div className="w-full flex flex-col items-center">
                <button>저장하기</button>
            </div>
        </div>
    )
}