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

const connectLinePlugin = {
  id: "connectLinePlugin",
  afterDatasetDraw(chart, args, pluginOptions) {
    const { ctx } = chart;
    const datasetMeta = chart.getDatasetMeta(0);

    datasetMeta.data.forEach((arc) => {
      const { x: centerX, y: centerY } = arc.getProps(["x", "y"], true);
      const { startAngle, endAngle, outerRadius } = arc.getProps(
        ["startAngle", "endAngle", "outerRadius"],
        true
      );

      // 중심각 계산
      const angle = (startAngle + endAngle) / 2;
      const labelX = centerX + Math.cos(angle) * (outerRadius + 20); // 라벨 위치 계산
      const labelY = centerY + Math.sin(angle) * (outerRadius + 20);

      // 선 그리기
      ctx.save();
      ctx.beginPath();
      ctx.moveTo(
        centerX + Math.cos(angle) * outerRadius,
        centerY + Math.sin(angle) * outerRadius
      ); // 파이조각 외곽점
      ctx.lineTo(labelX, labelY); // 라벨 포인트
      ctx.strokeStyle = arc.options.backgroundColor;
      ctx.lineWidth = 1;
      ctx.stroke();
      ctx.restore();
    });
  },
};

Chart.register(ChartDataLabels, connectLinePlugin);

export default function BudgetReport() {
  const [activeDate, setActiveDate] = useState(new Date());
  const [chartData, setChartData] = useState([]);
  const [categoriesList, setCategoriesList] = useState([]);
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

  // 카테고리 통계를 위한 api 연결
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await Api.get(
          `api/payment/statistics/category/${year}/${month}`
        );
        console.log("테스트:", response.data);
        if (response.data.status === "success") {
          const categoriesData = response.data.data.categories;
          console.log("예산통계 데이터:", categoriesData);
          // 퍼센트 기준 내림차순 정렬 (높은 퍼센트가 먼저 오도록)
          const sortedData = [...categoriesData].sort((a, b) => {
            // parseFloat을 사용해 문자열인 경우에도 올바르게 비교
            const percentA =
              typeof a.percentage === "string"
                ? parseFloat(a.percentage)
                : a.percentage;
            const percentB =
              typeof b.percentage === "string"
                ? parseFloat(b.percentage)
                : b.percentage;

            return percentB - percentA; // 내림차순 정렬
          });

          setChartData(sortedData);
          // 카테고리 목록은 ID 기준 정렬
          // 1. 각 데이터 항목에 해당 카테고리 ID 매핑
          const dataWithIds = categoriesData.map((item) => {
            const matchedCategory = categories.find(
              (c) => c.name === item.name
            );
            return {
              ...item,
              categoryId: matchedCategory ? matchedCategory.id : 999, // 매칭되지 않으면 높은 ID 부여
            };
          });

          // 2. ID 기준으로 정렬
          const idSortedData = [...dataWithIds].sort(
            (a, b) => a.categoryId - b.categoryId
          );
          setCategoriesList(idSortedData);
        }
      } catch (error) {
        console.error("요청 실패:", error);
      }
    };
    fetchData();
  }, [year, month]);
  useEffect(() => {
    if (!chartData.length) return;
    const ctx = chartRef.current.getContext("2d");

    // 기존 차트 제거
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
        layout: {
          padding: {
            top: 70,
            bottom: 70,
            left: 70,
            right: 70,
          },
        },
        plugins: {
          legend: {
            display: false,
          },
          datalabels: {
            color: "#000",
            font: {
              weight: "bold",
              size: 12,
            },
            anchor: "end", // 원 밖으로 위치
            align: "end", // 선 밖 끝에 정렬
            offset: 5, // 떨어지는 거리
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
            borderWidth: 1,
            borderRadius: 4,
            padding: 6,
          },
        },
      },
    });
  }, [chartData]);

  return (
    <div className="h-screen overflow-y-auto">
      <Container>
        <CustomBackHeader title="소비내역통계" />
        <div className="w-full bg-white rounded-lg shadow-sm">
          <MonthBar
            activeDate={activeDate}
            setActiveDate={setActiveDate}
            onYearMonthChange={({ year, month }) => {
              console.log("선택된 연/월:", year, month);
            }}
          />
        </div>
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
