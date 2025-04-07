export default function Box({ text, variant, children, onClick }) {
  const baseStyles =
    "w-full h-[50px] flex items-center justify-between rounded-md shadow-sm px-[10px]";
  const variants = {
    title: "font-medium text-black bg-white text-[19px]",
    highlight: "font-medium text-black bg-[#FFF0BA] text-[18px]",
  };
  // 유효성 검사
  const selectedVariant = variants[variant] ?? variants.title;
  return (
    <div className={`${baseStyles} ${selectedVariant}`} onClick={onClick}>
      <span>{text}</span>
      <div className="flex justify-end items-center w-auto">{children}</div>
    </div>
  );
}
