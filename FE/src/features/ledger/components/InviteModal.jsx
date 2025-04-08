import { useEffect, useRef, useState } from "react";

export default function Modal({ title, onClose, children }) {
  const modalRef = useRef(null);
  const [animationClass, setAnimationClass] = useState("translate-y-full");

  // 열기 애니메이션
  useEffect(() => {
    const timer = setTimeout(() => {
      setAnimationClass("translate-y-0");
    }, 10);
    return () => clearTimeout(timer);
  }, []);

  const handleClose = () => {
    // 닫기 애니메이션
    setAnimationClass("translate-y-full");
    setTimeout(() => {
      onClose(); // 부모에 알림
    }, 300); // transition 시간과 맞춤
  };

  return (
    <div
      ref={modalRef} // 모달 회색 배경
      className="fixed inset-0 z-10 bg-[#525252]/40 flex items-end justify-center"
    >
      <div // 모달 하얀색 박스
        className={`relative bg-white w-full h-[300px] max-w-md overflow-y-auto p-6 rounded-t-3xl transform transition-transform duration-300 flex flex-col ${animationClass}`}
      >
        {/* 닫기 버튼 - 우측 상단 */}
        <img
          className="absolute top-5 right-5 w-5 h-5 cursor-pointer"
          src="/close_icon.png"
          alt="닫기"
          onClick={handleClose}
        />

        {/* 타이틀과 설명 */}
        <div className="flex flex-col  justify-center mt-5 text-center">
          <div className="text-2xl font-bold">{title}</div>
        </div>

        {/* children은 아래쪽에 따로 */}
        <div className="mt-6 w-full">{children}</div>
      </div>
    </div>
  );
}
