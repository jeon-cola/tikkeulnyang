import React, { useState } from "react";
import Calendar from "react-calendar";
import "./CustomCalendar.css";
import "react-calendar/dist/Calendar.css";

export default function CustomCalendar({
  value,
  onChange,
  tileContent,
  tileClassName: customTileClassName,
  data,
}) {
  return (
    <div>
      <Calendar
        onChange={onChange}
        value={value}
        calendarType="hebrew" // 일요일부터 시작
        formatDay={(local, date) => <abbr>{date.getDate()}</abbr>} // 숫자+'일' 텍스트 '일'제거
        formatMonthYear={(local, date) =>
          `${date.getFullYear()}.${(date.getMonth() + 1)
            .toString()
            .padStart(2, "0")}`
        }
        tileContent={tileContent}
        tileClassName={({ date, view, activeStartDate }) => {
          // 해당 월의 일요일 대표색(#ff957a)으로 표시
          let classNames = [];

          if (view === "month") {
            const startMonth = activeStartDate.getMonth();
            const startYear = activeStartDate.getFullYear();
            const isCurrentMonth =
              date.getFullYear() === startYear &&
              date.getMonth() === startMonth;

            if (isCurrentMonth && date.getDay() === 0) {
              classNames.push("current-month-sunday");
            }
          }

          // 사용자 정의 클래스도 함께 적용 (optional)
          if (customTileClassName) {
            const customClass = customTileClassName({
              date,
              view,
              activeStartDate,
            });
            if (customClass) classNames.push(customClass);
          }

          return classNames.join(" ");
        }}
      />
    </div>
  );
}
