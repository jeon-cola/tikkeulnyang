import { useEffect, useRef, useState } from "react";
import Chart from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";
import CustomBackHeader from "@/components/CustomBackHeader";
import Container from "@/components/Container";
import MonthBar from "../MonthBar";
import Api from "../../../../services/Api";

import EntertainmentIcon from "../../assets/category/entertainment_icon.png";
import FoodIcon from "../../assets/category/food_icon.png";
import GoodsIcon from "../../assets/category/goods_icon.png";
import HousingIcon from "../../assets/category/housing_icon.png";
import MedicalIcon from "../../assets/category/medical_icon.png";
import ShoppingIcon from "../../assets/category/shopping_icon.png";
import TransportationIcon from "../../assets/category/transportation_icon.png";
import SpenseIcon from "../../assets/category/spense_icon.png";
import EducationIcon from "../../assets/category/education_icon.png";

Chart.register(ChartDataLabels);

const categories = [
  { id: 1, name: "주거/통신", Icon: HousingIcon },
  { id: 2, name: "식비", Icon: FoodIcon },
  { id: 3, name: "교통/차량", Icon: TransportationIcon },
  { id: 4, name: "교육/육아", Icon: EducationIcon },
  { id: 5, name: "쇼핑/미용", Icon: ShoppingIcon },
  { id: 6, name: "병원/약국", Icon: MedicalIcon },
  { id: 7, name: "문화/여가", Icon: EntertainmentIcon },
  { id: 8, name: "잡화", Icon: GoodsIcon },
  { id: 9, name: "결제", Icon: SpenseIcon },
];

export default function BudgetReport() {
  const [activeDate, setActiveDate] = useState(new Date());
  const [chartData, setChartData] = useState([]);
  const chartRef = useRef(null);
  const year = activeDate.getFullYear();
  const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");

  const backgroundColors = [
    "#ff957a",
    "#fdbb8e",
    "#FFE790",
    "#EAF3A0",
    "#D9F9BF",
    "#b2eee6",
    "#a7e9f4",
    "#aae1fe",
    "#e4d8ff",
    "#D9D9D9",
  ];

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await Api.get(
          `api/payment/statistics/category/${year}/${month}`
        );
        const categoriesData = response.data.data.categories;
        console.log("예산통계 데이터:", categoriesData);
        setChartData(categoriesData);
      } catch (error) {
        console.error("요청 실패:", error);
      }
    };
    fetchData();
  }, [year, month]);

  useEffect(() => {
    if (!chartData.length) return;
    const ctx = chartRef.current.getContext("2d");

    if (window.budgetChart) {
      window.budgetChart.destroy();
    }

    const total = chartData.reduce((sum, item) => sum + item.amount, 0);

    window.budgetChart = new Chart(ctx, {
      type: "pie",
      data: {
        labels: chartData.map((item) => item.name),
        datasets: [
          {
            label: "소비 금액",
            data: chartData.map((item) => item.amount),
            backgroundColor: backgroundColors,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false, // 기본 범례 제거
          },
          datalabels: {
            color: "#000",
            font: {
              weight: "bold",
              size: 14,
            },
            anchor: "end", // 원 밖으로 위치
            align: "end", // 선 밖 끝에 정렬
            offset: 20, // 떨어지는 거리
            formatter: (value, context) => {
              const label = context.chart.data.labels[context.dataIndex];
              const total = context.chart.data.datasets[0].data.reduce(
                (a, b) => a + b,
                0
              );
              const percentage = ((value / total) * 100).toFixed(1);
              return `${label}\n${percentage}%`;
            },
            // 아래 스타일은 선택 사항
            backgroundColor: "#ffffff",
            borderColor: "#cccccc",
            borderWidth: 1,
            borderRadius: 4,
            padding: 6,
          },
        },
      },
    });
  }, [chartData]);

  return (
    <div>
      <Container>
        <CustomBackHeader title="소비내역통계" />
        <MonthBar
          activeDate={activeDate}
          setActiveDate={setActiveDate}
          onYearMonthChange={({ year, month }) => {
            console.log("선택된 연/월:", year, month);
          }}
        />
        {/* 파이차트 */}
        <div className="w-full h-auto bg-white rounded-md shadow-sm px-4 py-3">
          <canvas
            id="pieChart"
            ref={chartRef}
            className="w-full max-w-[400px] h-[400px] mx-auto"
          ></canvas>
        </div>

        {/* 카테고리별 목록 */}
        <div className="bg-white w-full rounded-md shadow-sm px-4 py-3">
          <h2 className="text-lg font-semibold mb-3">카테고리별 소비 내역</h2>
          <ul className="space-y-2">
            {chartData.map((item, index) => {
              const matched = categories.find((c) => c.name === item.name);
              return (
                <li
                  key={index}
                  className="flex items-center justify-between pb-2"
                >
                  <div className="flex justify-between gap-4">
                    {/* 퍼센트 박스 */}
                    <div
                      className="w-[48px] h-[28px] text-sm flex items-center justify-center text-white font-medium rounded-md"
                      style={{ backgroundColor: backgroundColors[index] }}
                    >
                      {item.percentage}%
                    </div>

                    {/* 아이콘 및 카테고리네임 */}
                    <div className="flex justify-start gap-2 min-w-[120px]">
                      {matched && (
                        <img
                          src={matched.Icon}
                          alt={item.name}
                          className="w-6 h-6"
                        />
                      )}
                      <span className="text-gray-700">{item.name}</span>
                    </div>
                  </div>

                  {/* 금액 */}
                  <span className="text-medium font-medium text-gray-800">
                    {item.amount.toLocaleString()}원
                  </span>
                </li>
              );
            })}
          </ul>
        </div>
      </Container>
    </div>
  );
}
