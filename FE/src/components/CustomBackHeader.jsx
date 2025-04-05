import { useNavigate } from "react-router-dom";

export default function CustomBackHeader({ title, showCreateButton=false, navigate="/" }) {
  const nav = useNavigate();

  function handleClick(e) {
    e.preventDefault()
    nav(navigate)
  }

  function backHandler(e) {
    e.preventDefault();
    nav(-1);
  }

  return (
    <header className="w-full h-12 bg-white shadow-md fixed top-0 left-0 right-0 z-50 flex justify-center">
      <div className="absolute left-4 h-full flex items-center">
        <img
          src="/Back.svg"
          alt="뒤로가기"
          className="h-6 w-6"
          onClick={backHandler}
        />
      </div>

      <div className="h-full flex items-center">
        <p className="text-[23px] font-bold">{title}</p>
      </div>

      {showCreateButton &&        <div
        onClick={handleClick}
        className="fixed top-[25px] right-[30px] transform scale-y-[-1] z-[51]"
      >
        <div className="absolute w-[21px] h-0 border-black border-t-[2px]"></div>
        <div className="absolute w-[21px] h-0 border-black border-t-[2px] transform rotate-90"></div>
      </div>}
    </header>
  );
}
