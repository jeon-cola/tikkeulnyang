import { useEffect, useState } from "react"
import Step from "../assets/Step"
import step3Image from "../assets/step3.png"
import { useNavigate } from "react-router-dom";
import Api from "../../../services/Api";
import AlertModal from "../../../components/AlertModal";
export default function BucketListStep3() {
  const [stepCheck, setStepCheck] = useState({
    day:"",
    amount:""
  });
  const [dayList, setDayList] = useState([])
  const [isModal, setIsModal] = useState(false)
  const nav = useNavigate();


  // 입력 데이터 변경 로직직
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

  // 모달창 닫기기
  function isCloseModal() {
    setIsModal(false)
    nav("/bucketlist/list");
  }

  // 버킷리스트 생성 
  function creationHandler() {
    const fetchData = async() => {
      try {
        const response = await Api.post("api/bucket/date",{
          "saving_amount":stepCheck.amount,
          "saving_days":stepCheck.day
        })
        console.log(response.data)
        if (response.data.status === "success") {
          setIsModal(true)
        }
      } catch (error) {
        console.log(error)
      }
    }
    fetchData();
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await Api.get("api/bucket/days")
        console.log(response.data)
        if (response.data.status === "success")
          setDayList(response.data.data.days)
      } catch (error) {
        console.log(error)
      }
    }
    fetchData();
  },[])

  return(
    <div className="flex flex-col justify-center gap-4">

        {/* 단계 */}
        <Step currentStep={3} className="w-full"/>
        <p className="w-full font-semibold text-xl">매주 저축할 금액과 날짜를 선택해주세요</p>
        <img src={step3Image} alt="고양이 이미지" className="w-full scale-[1] transform-gpu"/>
        
        {/* 요일 선택 */}
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
          <p className="text-left" >요일을 선택해주세요</p>
          <select name="day" id="" className="w-full text-2xl font-semibold" value={stepCheck.day} onChange={inputHandler}>
            <option value="" >요일을 선택해주세요</option>
            {dayList.map((day,index)=> (
              <option value={day} key={index}>{day}</option>
            ))}
          </select>
        </div>

        {/* 금액 입력 */}
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex flex-col gap-1 p-[20px]">
          <p className="text-left">금액을 적어주세요</p>
          <input type="text" placeholder="30,000" className="w-full text-2xl font-semibold" onChange={inputHandler} name="amount" value={stepCheck.amount}/>
        </div>

        {/* 생성 버튼 */}
        <div className="w-full mx-auto flex flex-col items-center">
          <button 
            className="customButton hover:bg-blue-600 disabled:opacity-50 disabled:bg-gray-400 disabled:cursor-not-allowed mx-auto mb-[10px]" 
            disabled={!isChecked}
            onClick={creationHandler}
          >시작하기</button>
        </div>
        <AlertModal title="버킷리스트 생성" isClose={isCloseModal} isOpen={isModal} height={170}>
          <div>
            <p>버킷리스트 생성이 완료 되었습니다</p>
          </div>
        </AlertModal>
    </div>
  )
}
