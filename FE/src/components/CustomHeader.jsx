export default function CustomHeader({ title }) {
  return (
    <header className="w-full h-12 bg-white shadow-md rounded-t-2xl fixed top-0 left-0 right-0 flex-none order-none flex-grow-0 z-10 flex items-center">
      <p className="text-[17px] font-medium ml-6">{title}</p>
    </header>
  );
}
