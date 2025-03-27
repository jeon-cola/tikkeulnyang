export default function DigitalDevicePage ({title, current_savings, target_amount}) {
    const currentProgress = (target_amount/current_savings) *100 > 0 ? (target_amount/current_savings) *100 : 0
    return(
        <div className="w-full">
            <div>
                <div>
                    <p>#버킷리스트</p>
                    <p>{title}</p>
                </div>
                <img src="" alt="" />
            </div>
            <div>
                <button>저축하기</button>
            </div>
            <div>
                <div>
                    <span>{current_savings}</span> / <span>{target_amount}</span>
                </div>
                <p>{currentProgress}</p>
            </div>

            {/* 프로그레스 바 배경 */}
          <div className="absolute left-0 bottom-0 w-full h-[24px] bg-[#F1EFEF] border border-[#DFDFDF] rounded-[70px]">
            {/* 프로그레스 바 채움 */}
            <div
              className="h-full bg-[#FF957A] rounded-[70px]"
              style={{ width: `${currentProgress}%` }}
            ></div>
          </div>
        </div>
    )
}