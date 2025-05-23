import React, { useState } from "react";

export default function MonthBar({
  activeDate,
  setActiveDate,
  onYearMonthChange,
}) {
  const [isYearSelectorOpen, setIsYearSelectorOpen] = useState(false);

  // 좌우 화살표 클릭 시 월 변경 함수
  const handleMonthChange = (delta) => {
    const newDate = new Date(activeDate);
    newDate.setMonth(newDate.getMonth() + delta);
    setActiveDate(newDate);
    callYearMonthChange(newDate);
  };

  // 변경된 날짜 정보를 외부에 전달하는 함수
  const callYearMonthChange = (date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    if (onYearMonthChange) {
      onYearMonthChange({ year, month });
    }
  };

  // 연도 부분 클릭 시 드롭다운 토글
  const handleYearClick = () => {
    setIsYearSelectorOpen(!isYearSelectorOpen);
  };

  // 드롭다운에 표시할 연도 리스트 (현재 연도 기준 ±2년)
  const currentYear = activeDate.getFullYear();
  const yearOptions = [];
  for (let i = currentYear - 2; i <= currentYear + 2; i++) {
    yearOptions.push(i);
  }

  // 드롭다운에서 연도 선택 시 activeDate의 연도만 변경
  const handleYearSelect = (year) => {
    const newDate = new Date(activeDate);
    newDate.setFullYear(year);
    setActiveDate(newDate);
    setIsYearSelectorOpen(false);
    callYearMonthChange(newDate);
  };

  const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");

  return (
    <div className="text-2xl w-full relative flex items-center justify-between px-10 py-2">
      <button onClick={() => handleMonthChange(-1)} className="bg-transparent!">
        &lt;
      </button>
      <div className="flex text-[22px] items-center relative">
        <div
          onClick={handleYearClick}
          className=" font-medium text-left cursor-pointer select-none"
          style={{ flexGrow: 0 }}
        >
          {activeDate.getFullYear()}
        </div>
        {/* 월 표시 */}
        <div className=" font-medium ml-1">.{month}</div>
        {/* 연도 드롭다운 */}
        {isYearSelectorOpen && (
          <div className="absolute top-full mt-1 left-0 bg-white border border-gray-300 rounded shadow-md z-10">
            <ul>
              {yearOptions.map((year) => (
                <li
                  key={year}
                  onClick={() => handleYearSelect(year)}
                  className="px-4 py-2 cursor-pointer"
                >
                  {year}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
      <button onClick={() => handleMonthChange(1)} className="bg-transparent!">
        &gt;
      </button>
    </div>
  );
}
