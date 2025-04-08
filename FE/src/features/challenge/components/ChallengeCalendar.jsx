import { useState, useEffect } from "react";
import CustomCalendar from "@/components/CustomCalendar";

export default function ChallengeCalendar({ onDateRangeChange }) {
  const [value, setValue] = useState(new Date());
  const [dateRange, setDateRange] = useState([null, null]); // [시작일, 종료일]

  // 날짜 범위가 변경될 때마다 부모 컴포넌트에 전달
  useEffect(() => {
    if (onDateRangeChange) {
      onDateRangeChange(dateRange);
    }
  }, [dateRange]);

  const handleDateClick = (date) => {
    // date는 Date 객체이므로 문자열로 변환
    const formattedDate = date.toISOString().split("T")[0];

    // 날짜 범위 설정 로직
    if (!dateRange[0]) {
      // 시작일이 없으면 첫 번째 선택은 시작일로 설정
      setDateRange([formattedDate, null]);
    } else if (!dateRange[1]) {
      // 시작일이 있고 종료일이 없으면 두 번째 선택은 종료일로 설정
      // 선택한 날짜가 시작일보다 이전이면 순서 교체
      if (formattedDate < dateRange[0]) {
        setDateRange([formattedDate, dateRange[0]]);
      } else {
        setDateRange([dateRange[0], formattedDate]);
      }
    } else {
      // 이미 범위가 선택되어 있으면 새로운 선택으로 시작
      setDateRange([formattedDate, null]);
    }
  };

  // Tailwind CSS로 스타일 적용
  const tileClassName = ({ date, view }) => {
    if (view !== "month" || !dateRange[0]) return null;

    const formattedDate = date.toISOString().split("T")[0];
    let classes = "";

    // 시작일, 종료일, 범위 내 날짜 모두 동일한 스타일 적용
    if (formattedDate === dateRange[0]) {
      classes = "date-in-range";
    } else if (dateRange[1] && formattedDate === dateRange[1]) {
      classes = "date-in-range";
    } else if (
      // 시작일과 종료일 사이에 있는 날짜인 경우
      dateRange[1] &&
      formattedDate > dateRange[0] &&
      formattedDate < dateRange[1]
    ) {
      classes = "date-in-range";
    }

    return classes;
  };

  // 추가: 커스텀 스타일을 head에 주입
  const injectCustomStyles = () => {
    return (
      <style jsx global>{`
        /* 범위 내 날짜 스타일 - CustomCalendar.css 51-62번째 행과 동일하게 구현 */
        .date-in-range {
          position: relative;
          z-index: 0;
        }

        .date-in-range::before {
          content: "";
          position: absolute;
          top: 50%;
          left: 50%;
          width: 35px;
          height: 35px;
          background-color: #fff0ba;
          border-radius: 50%;
          transform: translate(-50%, -50%);
          z-index: -1;
          box-shadow: 1px 1px 4px 0px rgba(0, 0, 0, 20%);
          animation: growCircle 0.3s ease;
        }

        @keyframes growCircle {
          0% {
            transform: translate(-50%, -50%) scale(0);
          }
          100% {
            transform: translate(-50%, -50%) scale(1);
          }
        }

        /* 포커스 테두리 제거 */
        .react-calendar__tile:enabled:focus {
          outline: none !important;
          border: none !important;
          box-shadow: none !important;
        }

        /* 활성화된 타일에 대한 스타일 조정 */
        .react-calendar__tile--active {
          background: inherit !important;
          color: inherit !important;
        }

        /* 활성화된 타일에 대한 hover 스타일 */
        .react-calendar__tile--active:enabled:hover {
          background: inherit !important;
        }

        /* 포커스가 있는 타일에 대한 hover 스타일 */
        .react-calendar__tile:enabled:hover,
        .react-calendar__tile:enabled:focus {
          background-color: inherit !important;
        }
      `}</style>
    );
  };

  return (
    <div className="relative">
      {injectCustomStyles()}
      <CustomCalendar
        value={value}
        onChange={(date) => {
          setValue(date);
          handleDateClick(date);
        }}
        tileClassName={tileClassName}
      />

      <div className="mt-4">
        <p className="text-sm">
          선택된 기간: {dateRange[0] ? dateRange[0] : "시작일 미선택"} ~{" "}
          {dateRange[1] ? dateRange[1] : "종료일 미선택"}
        </p>
      </div>
    </div>
  );
}
