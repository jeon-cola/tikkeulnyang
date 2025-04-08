import { transform } from "framer-motion"
import { use, useEffect, useRef, useState } from "react"

export default function Password() {
  const [Password, setPassword] = useState("")
  const [buttonNumbers, setButtonNumbers] = useState("")
  const [isFail, setIsFail] = useState(false)
  const [pressButton, setPressButton] = useState([])

  // 지우기
  function deleteHandler() {
    setPassword(prev=> prev.slice(0,-1))
    setPressButton([11])
    setTimeout(()=>{setPressButton([])},200)
  }

  // 숫자 추가
  function numberClickHandler(number, index) {
    if (Password.length <6) {
      setPassword(prev=> prev+number)
      let otherButtonNumbers = Array.from({length:10},(_,i)=>i).filter(i=> i !== index)
      const randomNumber = otherButtonNumbers[Math.floor(Math.random()*otherButtonNumbers.length)]
      setPressButton([index,randomNumber])
      setTimeout(()=>{setPressButton([])},200)
    }
  }

  // 버튼 스타일
  function getButtonStyle(index) {
    return {
      backgroundColor: pressButton.includes(index) ? "#D9D9D9" : "white",
    }
  }

  // 전체 삭제 함수
  function allCleanHandler() {
    setPassword("")
    setPressButton([10])
    setTimeout(()=>{setPressButton([])},200)
  }

  // Fisher-Yates 알고리즘으로 랜덤 배열 생성
  useEffect(() => {
    function randomNumber() {
      const number = Array.from({length: 10}, (_, i) => i);
      for (let i = number.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        const temp = number[i]
        number[i] = number[j]
        number[j] = temp;
      }
      setButtonNumbers(number.join(""));
    }
    randomNumber();
  }, []);

  return(
    <div className="fixed flex flex-col inset-0 z-50 bg-white">

      <div className="flex-1 flex flex-col items-center justify-center">
        <div className="w-full">
          <p className="w-full font-bold text-2xl">비밀번호를 입력해주세요</p>
          <p className="text-[#D9D9D9] text-xl">숫자 6자리</p>
        </div>

        <div className="flex w-full justify-center gap-2 mt-10">
          {[...Array(6)].map((_,index) => (
            <span key={index} className={`rounded-full w-[10px] h-[10px] ${index < Password.length? "bg-black" : "bg-[#D9D9D9]"}`}/>
          ))}
        </div>
        {isFail?<p className="text-red-400 mt-1">비밀번호가 틀렸습니다</p> : ""}
      </div>

      <div className="w-full mt-auto mb-18">
        <div className="flex w-full h-16">
            <button className="w-1/3 h-full" style={getButtonStyle(0)} onClick={()=>numberClickHandler(buttonNumbers[0],0)}>{buttonNumbers[0]}</button>
            <button className="w-1/3 h-full" style={getButtonStyle(1)} onClick={()=>numberClickHandler(buttonNumbers[1],1)}>{buttonNumbers[1]}</button>
            <button className="w-1/3 h-full" style={getButtonStyle(2)} onClick={()=>numberClickHandler(buttonNumbers[2],2)}>{buttonNumbers[2]}</button>
        </div>
        <div className="flex w-full h-16">
            <button className="w-1/3 h-full" style={getButtonStyle(3)} onClick={()=>numberClickHandler(buttonNumbers[3],3)}>{buttonNumbers[3]}</button>
            <button className="w-1/3 h-full" style={getButtonStyle(4)} onClick={()=>numberClickHandler(buttonNumbers[4],4)}>{buttonNumbers[4]}</button>
            <button className="w-1/3 h-full" style={getButtonStyle(5)} onClick={()=>numberClickHandler(buttonNumbers[5],5)}>{buttonNumbers[5]}</button>
        </div>
        <div className="flex w-full h-16">
            <button className="w-1/3 h-full" style={getButtonStyle(6)} onClick={()=>numberClickHandler(buttonNumbers[6],6)}>{buttonNumbers[6]}</button>
            <button className="w-1/3 h-full" style={getButtonStyle(7)} onClick={()=>numberClickHandler(buttonNumbers[7],7)}>{buttonNumbers[7]}</button>
            <button className="w-1/3 h-full" style={getButtonStyle(8)} onClick={()=>numberClickHandler(buttonNumbers[8],8)}>{buttonNumbers[8]}</button>
        </div>
        <div className="flex w-full h-16">
            <button className="w-1/3 h-full" style={getButtonStyle(10)} onClick={allCleanHandler}>전체삭제</button>
            <button className="w-1/3 h-full" style={getButtonStyle(9)} onClick={()=>numberClickHandler(buttonNumbers[9],9)}>{buttonNumbers[9]}</button>
            <button className="w-1/3 h-full text-xl" style={getButtonStyle(11)} onClick={deleteHandler}>←</button>
        </div>
      </div>

    </div>
  )
}