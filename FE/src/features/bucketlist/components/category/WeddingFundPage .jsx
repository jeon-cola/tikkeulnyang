import Wedding from "../../assets/Wedding.png"

export default function WeddingFundPage ({title, current_savings, target_amount,color, onSaving, bucketListId}) {
    const currentProgress = current_savings > 0 && target_amount > 0 ? Math.min((current_savings / target_amount) * 100, 100) : 0

    // 저축 콜백
    function onButtonHandler(bucketListId) {
        onSaving(bucketListId)
    }

    return(
        <div className="w-full bg-white rounded-2xl shadow-md p-4 flex flex-col gap-3">
            <div className="w-full flex flex-row">
                <div className="w-full flex flex-col gap-6">
                    <p className="text-left font-semibold text-xl" style={{color:"#ED4C5C"}}>#버킷리스트</p>
                    <p className="text-left font-semibold text-2xl" style={{color:color}}>💍 {title}</p>
                </div>
                <img src={Wedding} alt="결혼 이미지" />
            </div>

            <div className="w-full">
                <button className="customButton" style={{backgroundColor:color}} onClick={()=> onButtonHandler(bucketListId)}>저축하기</button>
            </div>
            
            <div className="flex flex-row justify-between w-full">
                <div>
                    <span style={{color:color}} className="text-left font-semibold text-xl">{current_savings.toLocaleString()}</span> / <span style={{color:"#ED4C5C"}} className="text-left font-semibold text-xl">{target_amount.toLocaleString()}</span>
                </div >
                <p style={{color:color}} className="text-left font-semibold text-xl">{currentProgress.toFixed(1)}%</p>
            </div>

            {/* 프로그레스 바 배경 */}
          <div className="w-full h-[24px] bg-[#F1EFEF] border border-[#DFDFDF] rounded-[70px]">
            {/* 프로그레스 바 채움 */}
            <div
              className="h-full rounded-[70px]"
              style={{ width: `${currentProgress}%`, backgroundColor:"#ED4C5C" }}
            ></div>
          </div>
        </div>
    )
}