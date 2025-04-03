import React, { useEffect, useRef, useState } from "react";
import Chart from "chart.js/auto";
/**사용 예시
 * <RoundChart data={[50, 50]}></RoundChart>
 */
export default function RoundChart({ data = [75, 25] }) {
  const chartRef = useRef(null);
  const chartInstance = useRef(null);
  const [isVisible, setIsVisible] = useState(false);
  const containerRef = useRef(null);

  useEffect(() => {
    // 원형그래프 동적으로 보이게 하는 타이밍을 잡을 관찰자 등록
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
          observer.disconnect();
        }
      },
      {
        threshold: 0.1, // 요소가 10% 이상 보일 때 감지
      }
    );

    if (containerRef.current) {
      observer.observe(containerRef.current);
    }

    return () => {
      observer.disconnect();
    };
  }, []);

  useEffect(() => {
    if (!isVisible) return;

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
        duration: 1000, // 애니메이션 지속 시간 (밀리초)
        easing: "easeInOutQuart", // 애니메이션 이징 함수
        animateScale: true, // 크기 애니메이션 활성화
        animateRotate: true, // 회전 애니메이션 활성화
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
  }, [isVisible]);

  return (
    <>
      <div ref={containerRef} className="w-[148px] h-[148px]">
        <canvas ref={chartRef}></canvas>
      </div>
    </>
  );
}
