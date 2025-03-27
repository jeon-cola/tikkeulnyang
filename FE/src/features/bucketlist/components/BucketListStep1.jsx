import { useState } from "react";
import Step from "../assets/Step";
import step1image from "../assets/step1.png"
import { useNavigate } from "react-router-dom";
import Api from "../../../services/Api";

export default function BucketListStep1() {
    const [stepCheck, setStepCheck] = useState({
        "category":"",
        "title":"",
        "amount":""
    });

    // 사용자 선택 로직
    function inputHandler(e){
        const {name, value} = e.target;
        setStepCheck({
            ...stepCheck,
            [name]:value
        });
    };

    // 숫자열만 들어있는지를 확인
    function isNumber(str) {
        return /^\d+$/.test(str);
    };

    // 선택지 모두 선택했는지 확인
    const isChecked = stepCheck.category && stepCheck.title && isNumber(stepCheck.amount);

    // 등록 로직
    function nextHandler() {
        const fetchData = async () => {
            try {
                const response = await Api.post("api/bucket", {
                    "category":stepCheck.category,
                    "title":stepCheck.title,
                    "amount": stepCheck.amount
                })
                console.log(response.data)
                if (response.status === "scuccess") {
                    nav("/bucketlist/step2")
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData();
    }
    return(
        <div className="flex flex-col justify-center gap-4">
            <Step currentStep="1" className="w-full"/>
            <img src={step1image} alt="고양이 이미지" className="w-full scale-[1] transform-gpu"/>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
                <p className="text-left">버킷리스트 카테고리를 선택해주세요</p>
                <select name="category" id="" className="w-full text-2xl font-semibold" onChange={inputHandler} value={stepCheck.category}>
                    <option value="" disabled>옵션을 선택해주세요</option>
                    <option value="1" >교통/차량</option>
                    <option value="2" >쇼핑/미용</option>
                    <option value="3" >교육/육아</option>
                    <option value="4" >주거/통신</option>
                    <option value="5" >문화/여가</option>
                    <option value="6" >병원/약국</option>
                    <option value="7" >식비</option>
                    <option value="8" >잡화</option>
                    <option value="9" >결제</option>
                </select>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
                <p className="text-left font-normal">버킷리스트를 작성해 주세요</p>
                <input type="text" className="w-full text-2xl font-semibold" onChange={inputHandler} name="title" value={stepCheck.title} placeholder="일본 여행가기"/>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
                <p className="text-left font-normal">금액을 적어주세요</p>
                <input type="text" className="w-full text-2xl font-semibold" name="amount" value={stepCheck.amount} onChange={inputHandler} placeholder="3,000,000"/>
            </div>
            <div className="w-full mx-auto flex flex-col items-center">
                <button 
                    className="hover:bg-blue-600 disabled:opacity-50 disabled:bg-gray-400 disabled:cursor-not-allowed mx-auto mb-[10px]" 
                    disabled={!isChecked} 
                    onClick={nextHandler}
                    >
                        다음
                </button>
            </div>
        </div>
    )
}
