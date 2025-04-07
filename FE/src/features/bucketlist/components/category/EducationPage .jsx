import Education from "../../assets/Education.png"
import MoreIcon from "../../assets/MoreIcon.png"

export default function EducationPage ({title, current_savings, target_amount,color, onSaving, bucketListId, onDelete}) {
    const currentProgress = current_savings > 0 && target_amount > 0 ? Math.min((current_savings / target_amount) * 100, 100) : 0
    
    // 저축 콜백
    function onButtonHandler(bucketListId) {
        onSaving(bucketListId)
    }

    // 삭제
    function deleteHandler(bucketListId) {
        onDelete(bucketListId)
    }

    return(
         <div className="w-full bg-white rounded-2xl shadow-md p-4 flex flex-col gap-3 relative">
             <img src={MoreIcon} alt="더보기 버튼" className="w-[30px] h-[30px] absolute top-2 right-2" onClick={deleteHandler}/>
            <div className="w-full flex flex-row">
                <div className="w-full flex flex-col gap-6">
                    <p className="text-left font-semibold text-xl" style={{color:"#83BF4F"}}>#버킷리스트</p>
                    <p className="text-left font-semibold text-2xl" style={{color:color}}>📚 {title}</p>
                </div>
                <img src={Education} alt="교육비 이미지" />
            </div>
            <div className="w-full">
                <button className="customButton" style={{backgroundColor:color}} onClick={()=>onButtonHandler(bucketListId)} >저축하기</button>
            </div>
            <div className="flex flex-row justify-between w-full">
                <div>
                    <span style={{color:color}} className="text-left font-semibold text-xl">{current_savings.toLocaleString()}</span> / <span style={{color:"#83BF4F"}}  className="text-left font-semibold text-xl">{target_amount.toLocaleString()}</span>
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