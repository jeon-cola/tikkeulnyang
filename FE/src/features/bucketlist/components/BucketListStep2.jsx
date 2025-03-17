import { useState } from "react"
import Step from "../assets/step"
import step2Image from "../assets/step2.png"

export default function BucketListStep2() {
    const [stepCheck,setStepCheck] = useState({
        withdrawl_amount:"",
        withdrawl_amount_num:"",
        saving_account:"",
        saving_account_num:""
    });

    //입력 변경
    function inputHandler(e) {
        const {name, value} = e.target;
        console.log(name,value)
        setStepCheck({
            ...stepCheck,
            [name]:value
        })
    }

    return(
        <div className="flex flex-col justify-center gap-4">
            <Step currentStep={2}/>
            <img src={step2Image} alt="고양이 사진" className="w-full scale-[1] transform-gpu mb-[30px]" />
            <div>
                <p className="text-left">주거래 계좌를 선택해주세요</p>
                <select name="saving_account" id="" className="w-full" onChange={inputHandler} value={stepCheck.saving_account}>
                    <option value="" disabled>옵션을 선택해주세요</option>
                    <option value="신한 저축 은행">신한 저축 은행</option>
                </select>
                <input type="text" placeholder="111-1111-111" className="text-left w-full" value={stepCheck.saving_account_num} name="saving_account_num" onChange={inputHandler}/>
            </div>
            <div>
                <p className="text-left">저축할 계좌를 선택해주세요</p>
                <select name="withdrawl_amount" id="" className="w-full" onChange={inputHandler} value={stepCheck.withdrawl_amount}>
                    <option value="" disabled>옵션을 선택해주세요</option>
                    <option value="국민 은행">국민은행</option>
                </select>
                <input type="text" placeholder="111-1111-111" className="text-left w-full" value={stepCheck.withdrawl_amount_num} name="withdrawl_amount_num" onChange={inputHandler}/>
            </div>
            <div className="w-full mx-auto flex flex-col items-center">
                <button 
                    className="hover:bg-blue-600 disabled:opacity-50 disabled:bg-gray-400 disabled:cursor-not-allowed mx-auto" 
                    // disabled={!isChecked} 
                    // onClick={nextHandler}
                    >
                        다음
                </button>
            </div>
        </div>
    )
}