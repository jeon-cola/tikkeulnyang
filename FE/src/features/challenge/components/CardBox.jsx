import React from "react";

export default function CardBox({ children }) {
  return (
    <>
      <div className="flex flex-row justify-around items-center p-0 gap-[30px] w-full h-[207px] flex-none">
        {children}
      </div>
    </>
  );
}
