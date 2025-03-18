import React, { useEffect, useRef } from "react";
import Chart from "chart.js/auto";
/**사용 예시
 * <RoundChart data={[50, 50]}></RoundChart>
 */
export default function RoundChart({ data = [75, 25] }) {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);

  useEffect(() => {
    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    // 차트 데이터 설정
    const chartData = {
      labels: ["A", "B"], // 필요에 따라 props로 받을 수 있음
      datasets: [
        {
          data: data,
          backgroundColor: ["#FF957A", "#DFDFDF"],
          borderColor: ["#FF957A", "#DFDFDF"],
          borderWidth: 0,
          hoverOffset: 0,
        },
      ],
    };

    // 차트 옵션 설정
    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      animation: {
        duration: 0,
      },
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          enabled: false,
        },
      },
      cutout: 50, // 도넛의 중앙 구멍 크기(백분율)
    };

    // 새 차트 인스턴스 생성
    const ctx = chartRef.current.getContext("2d");
    chartInstance.current = new Chart(ctx, {
      type: "doughnut",
      data: chartData,
      options: chartOptions,
    });

    // 언마운트가 될 때 정리
    return () => {
      if (chartInstance.current) {
        chartInstance.current.destroy();
      }
    };
  }, []);

  return (
    <>
      <div className="w-[148px] h-[148px]">
        <canvas ref={chartRef}></canvas>
      </div>
    </>
  );
}
