import { useEffect, useRef, useState } from "react";

export default function AlertModal({
  title,
  isOpen,
  isClose,
  children,
  height,
  onConfirm,
  onCancel,
  showCancelButton = false, // true일 때만 확인/취소 버튼 2개
}) {
  const modalRef = useRef(null);
  const [animationClass, setAnimationClass] = useState("opacity-0");

  // 모달 열기 애니메이션
  useEffect(() => {
    if (isOpen) {
      const timer = setTimeout(() => {
        setAnimationClass("opacity-100");
      }, 10);
      return () => clearTimeout(timer);
    } else {
      setAnimationClass("opacity-0");
    }
  }, [isOpen]);

  // 닫기 애니메이션
  const handleClose = () => {
    setAnimationClass("opacity-0");
    setTimeout(() => {
      isClose();
    }, 250);
  };

  if (!isOpen) return null;

  return (
    <div
      ref={modalRef}
      className="fixed inset-0 z-10 bg-[#666666]/40 flex items-center justify-center"
    >
      <div
        className={`relative bg-white w-full m-[20px] rounded-3xl transform transition-transform duration-300 flex flex-col ${animationClass} gap-3 p-2`}
        style={{ height: height ? `${height}px` : "auto" }}
      >
        {/* 타이틀 */}
        <h2 className="text-2xl font-semibold mt-2">{title}</h2>

        {/* 내용 */}
        <div className="flex-1 flex flex-col items-center justify-center mb-10 ">
          {children}
        </div>

        {/* 버튼 */}
        <div className="flex justify-center gap-4 w-full px-4 mb-3">
          {showCancelButton ? (
            <div className="absolute bottom-6 left-0 right-0 px-4 flex justify-center gap-4">
              <button
                className="flex-1 bg-gray-200! rounded-lg py-2 font-medium"
                onClick={() => {
                  if (onCancel) onCancel();
                  handleClose();
                }}
              >
                취소
              </button>
              <button
                className="flex-1 bg-black text-white rounded-lg py-2 font-medium"
                onClick={() => {
                  if (onConfirm) onConfirm();
                  handleClose();
                }}
              >
                확인
              </button>
            </div>
          ) : (
            <button
              className="customButton absolute bottom-3"
              onClick={handleClose}
            >
              확인
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
