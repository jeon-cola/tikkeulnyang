import { useEffect, useRef, useState } from "react";
import Chart from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";
import CustomBackHeader from "@/components/CustomBackHeader";
import Container from "@/components/Container";
import MonthBar from "../MonthBar";
import Api from "../../../../services/Api";
import CategoryList from "../CategoryList";
import IsLoading from "@/components/IsLoading";

const categories = CategoryList();
Chart.register(ChartDataLabels);

export default function BudgetReport() {
  const [isLoading, setIsLoading] = useState(true);
  const [activeDate, setActiveDate] = useState(new Date());
  const [chartData, setChartData] = useState([]);
  const [categoriesList, setCategoriesList] = useState([]);
  const chartRef = useRef(null);
  const chartInstance = useRef(null); // 🔥 useRef로 chart 인스턴스 관리

  const year = activeDate.getFullYear();
  const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");

  const backgroundColors = [
    "#ff957a", "#fdbb8e", "#FFE790", "#EAF3A0", "#D9F9BF",
    "#b2eee6", "#a7e9f4", "#aae1fe", "#e4d8ff", "#D9D9D9",
  ];

  // ✅ 차트 그리는 함수
  const drawChart = (data) => {
    const ctx = chartRef.current.getContext("2d");

    if (chartInstance.current) {
      chartInstance.current.destroy();
    }

    chartInstance.current = new Chart(ctx, {
      type: "pie",
      data: {
        labels: data.map((item) => item.name),
        datasets: [
          {
            label: "소비 금액",
            data: data.map((item) => item.amount),
            backgroundColor: backgroundColors,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        layout: {
          padding: { top: 20, bottom: 20, left: 20, right: 20 },
        },
        plugins: {
          legend: {
            display: true,
            position: "bottom",
            labels: {
              boxWidth: 15,
              padding: 20,
              font: { size: 12 },
              maxWidth: 130,
              usePointStyle: true,
            },
          },
          datalabels: { display: false },
        },
      },
    });
  };

  // ✅ 차트 데이터 변경될 때 차트 그리기
  useEffect(() => {
    if (chartData.length > 0 && chartRef.current) {
      drawChart(chartData);
    }
  }, [chartData]);

  // ✅ API 호출 및 데이터 세팅
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await Api.get(
          `api/payment/statistics/category/${year}/${month}`
        );
        console.log(response.data);
        if (response.data.status === "success") {
          const categoriesData = response.data.data.categories;

          const sortedData = [...categoriesData].sort((a, b) => {
            const percentA = parseFloat(a.percentage);
            const percentB = parseFloat(b.percentage);
            return percentB - percentA;
          });

          setChartData(sortedData); // 여기까지만 하고 drawChart는 useEffect에서 처리

          const dataWithIds = categoriesData.map((item) => {
            const matchedCategory = categories.find((c) => c.name === item.name);
            return {
              ...item,
              categoryId: matchedCategory ? matchedCategory.id : 999,
            };
          });

          const idSortedData = [...dataWithIds].sort(
            (a, b) => a.categoryId - b.categoryId
          );
          setCategoriesList(idSortedData);
        }
      } catch (error) {
        console.error("요청 실패:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [year, month]);

  if (isLoading) return <IsLoading />;

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
            className="w-full max-w-[500px] h-[500px] mx-auto"
            width={500}
            height={500}
          ></canvas>
        </div>

        {/* 카테고리별 목록 */}
        <div className="bg-white w-full rounded-md shadow-sm px-4 py-3 mt-4">
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
                    <div
                      className="w-[48px] h-[28px] text-sm flex items-center justify-center text-white font-medium rounded-md"
                      style={{
                        backgroundColor:
                          backgroundColors[index % backgroundColors.length],
                      }}
                    >
                      {item.percentage}%
                    </div>
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
