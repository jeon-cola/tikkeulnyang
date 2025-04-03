export default function Container({ children }) {
  return (
    <div className="flex flex-col items-start gap-3 relative w-full min-h-screen left-0 top-[49px] bg-[#F7F7F7]">
      {children}
    </div>
  );
}
