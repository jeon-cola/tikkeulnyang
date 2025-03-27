import { useNavigate } from "react-router-dom"

export default function BucketListInformation() {
    const nav = useNavigate();

    // 생성 페이지로 이동동
    function stepHandler(e) {
        e.preventDefault();
        nav("/bucketlist/step1")
    }
    return (
        <div className="flex flex-col justify-center gap-5">
            <img src="/bucketListImg_1.png" alt="안내문 사진 1" className="w-full scale-[1.2] transform-gpu mb-[30px]"/>
            <div>
                <p className="font-semibold text-2xl">목표를 향한</p>
                <p className="font-semibold text-2xl">나만의 버킷리스트</p>
            </div>
            <div>
                <p>나만의 목표 달성을 위한</p>
                <p>버킷리스트를 만들어 보세요요</p>
            </div>
            <div className="w-full h-[128px] bg-[#FF957A] rounded-[25px] flex items-center justify-between px-7">
                <div className="text-white text-left">
                    <p className="font-semibold text-2xl">일본 여행가기</p>
                    <p className="font-semibold text-2xl">300만원 모으기</p>
                </div>
                <img src="/Plane.png" alt="비행기" className="w-25 h-25" />
            </div>
            
            <div className="w-full h-[128px] bg-[#FF957A] rounded-[25px] flex items-center justify-between px-7">
                <div className="text-white text-left">
                    <p className="font-semibold text-2xl">자취방 마련하기</p>
                    <p className="font-semibold text-2xl">500만원 모으기</p>
                </div>
                <img src="/Furniture.png" alt="방" className="w-25 h-25" />
            </div>
            <div>
                <p className="font-semibold text-2xl">출석으로</p>
                <p className="font-semibold text-2xl">목표달성을 한눈에 보기기</p>
            </div>
            <div>
                <p>요일과 매주 저축할 금액을 정해</p>
                <p>간편하게 송금해 보세요요</p>
            </div>
            <img src="/cat_stamp.png" alt="고양이 스탬프" className="w-full scale-[1.2] transform-gpu" />
            <div className="w-full mx-auto flex flex-col items-center mb-[10px]">
                <button className="customButton"onClick={stepHandler}>생성하기</button>
            </div>
        </div>
    ) 
}