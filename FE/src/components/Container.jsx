export default function Container({ children }) {
  return (
    <div className="flex flex-col items-start gap-3 relative w-full min-h-screen bg-[#F7F7F7] pb-4 pt-[49px]">
      {children}
    </div>
  );
}
