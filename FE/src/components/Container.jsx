export default function Container({ children }) {
  return (
    <div className="flex flex-col items-start p-[30px_20px_12px] gap-3 relative w-full min-h-screen left-0 top-[49px] overflow-y-scroll bg-[#F7F7F7]">
      {children}
    </div>
  );
}
