import { useState } from "react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";

export default function CustomCalendar() {
  const [value, setValue] = useState(new Date()); // useState에서 타입

  return (
    <div>
      <Calendar onChange={setValue} value={value} />
      <div>
        선택한 날짜:{" "}
        {Array.isArray(value)
          ? `${value[0].toDateString()} ~ ${value[1].toDateString()}`
          : value.toDateString()}
      </div>
    </div>
  );
}
