export default function Box({ text, variant, children }) {
  const baseStyles = "w-full h-[40px] flex items-center justify-between rounded-md shadow-sm pl-[10px] pr-[10px]"
  const variants = {
    title: "font-bold text-black bg-white",
    highlight: "font-medium text-black bg-[#FFF0BA]"
  }
  return (
  <div className={`${baseStyles} ${variants[variant]}`}>
      <span>{text}</span>
      <div className="flex justify-end items-center w-auto">{children}</div>
          
  </div>
  )
}

