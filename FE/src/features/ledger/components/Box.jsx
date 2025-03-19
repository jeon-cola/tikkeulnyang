export default function Box({ text }) {
  return (
    <div className="w-full h-[40px] bg-white flex items-center justify-left rounded-md shadow-sm pl-[10px]">
      {text}
    </div>
  );
}
