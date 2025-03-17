import { useState } from "react"
import Step from "../assets/step"
import step3Image from "../assets/step3.png"
import { useNavigate } from "react-router-dom";
export default function BucketListStep3() {
  const [stepCheck, setStepCheck] = useState({
    day:"",
    amount:""
  });

  const nav = useNavigate();

  function inputHandler(e) {
    const {name,value} = e.target;
    setStepCheck({
      ...stepCheck,
      [name]:value
    });
  };

   // 숫자열만 들어있는지를 확인
   function isNumber(str) {
    return /^\d+$/.test(str);
};


  const isChecked = isNumber(stepCheck.amount) && stepCheck.day;

  function creationHandler() {
    nav("/bucketlist/list");
  };

  return(
    <div className="flex flex-col justify-center gap-4">
        <Step currentStep={3} className="w-full"/>
        <p className="w-full font-semibold text-xl">매주 저축할 금액과 날짜를 선택해주세요</p>
        <img src={step3Image} alt="고양이 이미지" className="w-full scale-[1] transform-gpu"/>
        
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
          <p className="text-left" >요일을 선택해주세요</p>
          <select name="day" id="" className="w-full text-2xl font-semibold" value={stepCheck.day} onChange={inputHandler}>
            <option value="" >요일을 선택해주세요</option>
            <option value="월요일">월요일</option>
            <option value="화요일">화요일</option>
            <option value="수요일">수요일</option>
            <option value="목요일">목요일</option>
            <option value="금요일">금요일</option>
            <option value="토요일">토요일</option>
            <option value="일요일">일요일</option>
          </select>
        </div>

        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
          <p className="text-left">금액을 적어주세요</p>
          <input type="text" placeholder="30,000" className="w-full text-2xl font-semibold" onChange={inputHandler} name="amount" value={stepCheck.amount}/>
        </div>

        <div className="w-full mx-auto flex flex-col items-center">
          <button 
            className="hover:bg-blue-600 disabled:opacity-50 disabled:bg-gray-400 disabled:cursor-not-allowed mx-auto mb-[10px]" 
            disabled={!isChecked}
            onClick={creationHandler}
          >시작하기</button>
        </div>
    </div>
  )
}