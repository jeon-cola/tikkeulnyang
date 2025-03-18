// App.jsx
import React, { useState } from "react";
import Calendar from "react-calendar";
// import "./Calendar.css";
import "react-calendar/dist/Calendar.css";
import "./CustomCalendar.css";

export default function CustomCalendar() {
  const [value, setValue] = useState(new Date());

  return (
    <div>
      <Calendar
        onChange={setValue}
        value={value}
        calendarType="hebrew"
        formatDay={(local, date) => date.getDate()}
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
