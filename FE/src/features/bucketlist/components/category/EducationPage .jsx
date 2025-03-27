import Education from "../../assets/Education.png"

export default function EducationPage ({title, current_savings, target_amount,color}) {
    const currentProgress = current_savings > 0 && target_amount > 0 ? Math.min((current_savings / target_amount) * 100, 100) : 0
    return(
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] p-4 flex flex-col gap-3">
            <div className="w-full flex flex-row">
                <div className="w-full flex flex-col gap-6">
                    <p className="text-left font-semibold text-xl" style={{color:"#83BF4F"}}>#버킷리스트</p>
                    <p className="text-left font-semibold text-2xl" style={{color:color}}>📚 {title}</p>
                </div>
                <img src={Education} alt="교육비 이미지" />
            </div>
            <div className="w-full">
                <button className="customButton" style={{backgroundColor:color}}>저축하기</button>
            </div>
            <div className="flex flex-row justify-between w-full">
                <div>
                    <span style={{color:color}} className="text-left font-semibold text-xl">{current_savings}</span> / <span style={{color:"#83BF4F"}}  className="text-left font-semibold text-xl">{target_amount}</span>
                </div >
                <p style={{color:color}} className="text-left font-semibold text-xl">{currentProgress.toFixed(1)}%</p>
            </div>

            {/* 프로그레스 바 배경 */}
          <div className="w-full h-[24px] bg-[#F1EFEF] border border-[#DFDFDF] rounded-[70px]">
            {/* 프로그레스 바 채움 */}
            <div
              className="h-full rounded-[70px]"
              style={{ width: `${currentProgress}%`, backgroundColor:"#83BF4F" }}
            ></div>
          </div>
        </div>
    )
}