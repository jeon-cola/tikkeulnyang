export default function CustomBackHeader({ title }) {
    return (
      <header className="w-full h-12 bg-white shadow-md fixed top-0 left-0 right-0 z-50 flex justify-center">
        <div className="absolute left-4 h-full flex items-center">
          <img src="/Back.svg" alt="뒤로가기" className="h-6 w-6" />
        </div>
        
        <div className="h-full flex items-center">
          <p className="text-[23px] font-bold">{title}</p>
        </div>
      </header>
    );
  }