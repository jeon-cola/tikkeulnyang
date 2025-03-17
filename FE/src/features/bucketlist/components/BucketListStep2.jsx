import { useState } from "react"
import Step from "../assets/step"
import step2Image from "../assets/step2.png"
import { useNavigate } from "react-router-dom";

export default function BucketListStep2() {
    const [stepCheck,setStepCheck] = useState({
        withdrawl_amount:"",
        withdrawl_amount_num:"",
        saving_account:"",
        saving_account_num:""
    });
    const nav = useNavigate();

    //입력 변경
    function inputHandler(e) {
        const {name, value} = e.target;
        setStepCheck({
            ...stepCheck,
            [name]:value
        });
    };
    
    function nextHandler(e) {
        e.preventDefault();
        nav("/bucketlist/step3")
    } 

    const isChecked = 
        stepCheck.withdrawl_amount && 
        stepCheck.withdrawl_amount_num && 
        stepCheck.saving_account && 
        stepCheck.saving_account_num

    return(
        <div className="flex flex-col justify-center gap-4">
            <Step currentStep={2}/>
            <p className="w-full font-semibold text-xl">출근 통장과 저축통장을 선택해주세요</p>
            <img src={step2Image} alt="고양이 사진" className="w-full scale-[1] transform-gpu" />
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
                <p className="text-left">주거래 계좌를 선택해주세요</p>
                <select name="saving_account" id="" className="w-full text-xl font-semibold" onChange={inputHandler} value={stepCheck.saving_account}>
                    <option value="" disabled>옵션을 선택해주세요</option>
                    <option value="신한 저축 은행">신한 저축 은행</option>
                </select>
                <input type="text" placeholder="계좌번호를 입력해주세요" className="text-left w-full text-xl font-semibold" value={stepCheck.saving_account_num} name="saving_account_num" onChange={inputHandler}/>
            </div>
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
                <p className="text-left">저축할 계좌를 선택해주세요</p>
                <select name="withdrawl_amount" id="" className="w-full text-xl font-semibold" onChange={inputHandler} value={stepCheck.withdrawl_amount}>
                    <option value="" disabled>옵션을 선택해주세요</option>
                    <option value="국민 은행">국민은행</option>
                </select>
                <input type="text" placeholder="계좌번호를 입력해주세요" className="text-left w-full text-xl font-semibold" value={stepCheck.withdrawl_amount_num} name="withdrawl_amount_num" onChange={inputHandler}/>
            </div>
            <div className="w-full mx-auto flex flex-col items-center">
                <button 
                    className="hover:bg-blue-600 disabled:opacity-50 disabled:bg-gray-400 disabled:cursor-not-allowed mx-auto" 
                     disabled={!isChecked} 
                     onClick={nextHandler}
                    >
                        다음
                </button>
            </div>
        </div>
    )
}