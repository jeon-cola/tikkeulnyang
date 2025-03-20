import React, { useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import "./CustomCalendar.css";

export default function CustomCalendar() {
  const [value, setValue] = useState(new Date());

  return (
    <div className="max-w-[320px]">
      <Calendar
        onChange={setValue}
        value={value}
        // 일요일 기준으로 정렬
        calendarType="hebrew"
        // 달력 일에 숫자일 표시를 숫자로 보이게 하는 속성성
        formatDay={(local, date) => date.getDate()}
        tileClassName={({ date, view, activeStartDate }) => {
          // 'month' 뷰에서만 스타일 지정
          if (view === "month") {
            const startMonth = activeStartDate.getMonth();
            const startYear = activeStartDate.getFullYear();

            // 현재 렌더링 중인 달과 같은 연/월 && 요일이 일요일이면
            if (
              date.getFullYear() === startYear &&
              date.getMonth() === startMonth &&
              date.getDay() === 0
            ) {
              return "current-month-sunday";
            }
          }
          return null;
        }}
      />
      <div>
        선택한 날짜:{" "}
        {Array.isArray(value)
          ? `${value[0].toDateString()} ~ ${value[1].toDateString()}`
          : value.toDateString()}
      </div>
    </div>
  );
}
