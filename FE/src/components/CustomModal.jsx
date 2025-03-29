import { useEffect, useRef, useState } from "react";

export default function CustomModal({isOpen, onClose, title,children}) {
  const modalRef = useRef(null);
  const [animationClass, setAnimationClass] = useState("translate-y-full");

  // 열기 애니메이션
  useEffect(() => {
    if (isOpen) {
        const timer = setTimeout(() => {
          setAnimationClass("translate-y-0");
        }, 10);
        return () => clearTimeout(timer);
    } else {
        setAnimationClass("translate-y-full")
    }
  }, [isOpen]);

  const handleClose = () => {
    // 닫기 애니메이션
    setAnimationClass("translate-y-full");
    setTimeout(() => {
      onClose(); // 부모에 알림
    }, 300); // transition 시간과 맞춤
  };
  if (!isOpen) return null
  return (
    <div
      ref={modalRef} // 모달 회색 배경
      className="fixed inset-0 z-10 bg-[#525252]/40 flex items-end justify-center"
    >
      <div // 모달 하얀색 박스
        className={`relative bg-white w-full min-h-[460px] max-w-md p-3 rounded-t-3xl transform transition-transform duration-300 flex flex-col ${animationClass} gap-3`}
      >
        <div className="w-full flex flex-row justify-center">
            <h2 className="text-2xl font-semibold">{title}</h2>
            {/* 닫기 버튼 - 우측 상단 */}
            <img
            className="absolute top-5 right-5 w-5 h-5 cursor-pointer"
            src="/close_icon.png"
            alt="닫기"
            onClick={handleClose}
            />
        </div>
        {children}
      </div>
    </div>
  );
}
