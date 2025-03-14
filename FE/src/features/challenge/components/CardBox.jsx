import React from "react";

export default function CardBox({ children }) {
  return (
    <>
      <div className="flex flex-row justify-between items-center p-0 gap-[30px] w-[366px] h-[207px] flex-none">
        {children}
      </div>
    </>
  );
}
