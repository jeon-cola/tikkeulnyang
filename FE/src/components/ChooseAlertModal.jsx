import { useEffect, useRef, useState } from "react"

export default function ChooseAlertModal({title, isOpen, isClose, children, height, isFunctionHandler}) {
    const modalRef = useRef(null)
    const [animationClass, setAnimationClass] = useState("opacity-0")
    
    // 모달 열기
    useEffect(() => {
        if (isOpen){
            const timer = setTimeout(() => {
                setAnimationClass("opacity-100")
            }, 10)
            return () => clearTimeout(timer)
        } else {
            setAnimationClass("opacity-0")
        }
    }, [isOpen])

    // 닫기 애니메이션
    function handleClose() {
        setAnimationClass("opacity-0")
        setTimeout(() => {
            isClose()
        }, 250)
    }

    // 함수 실행 
    function functionHandler() {
        setAnimationClass("opacity-0")
        setTimeout(() => {
            isFunctionHandler()
            isClose()
        }, 250)
    }
    
    if (!isOpen) return null
    
    return (
        <div ref={modalRef} className="fixed inset-0 z-10 bg-[#666666]/40 flex items-center justify-center">
            <div 
                className={`relative bg-white w-full m-[20px] rounded-3xl flex flex-col ${animationClass} gap-3 p-4`}
                style={{ 
                    height: height ? `${height}px` : 'auto',
                    transition: 'opacity 0.25s ease'
                }}
            >
                <h2 className="text-2xl font-semibold">{title}</h2>
                <div className="mt-3 flex-1">
                    {children}
                </div>
                <div className="flex justify-between px-7 pb-3">
                    <button className="customWhiteButton" onClick={handleClose}>취소</button>
                    <button className="customButton" onClick={functionHandler}>확인</button>
                </div>
            </div>
        </div>
    )
}