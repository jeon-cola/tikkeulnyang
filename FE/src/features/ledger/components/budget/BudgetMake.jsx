import { useEffect, useState } from "react";
import Container from "@/components/Container";
import CustomBackHeader from "@/components/CustomBackHeader";
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

export default function BudgetMake() {
  const [activeDate, setActiveDate] = useState(new Date());
  const [budgetData, setBudgetData] = useState([]);
  const [isFirstBudget, setIsFirstBudget] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [totalBudget, setTotalBudget] = useState(0);

  const year = activeDate.getFullYear();
  const month = (activeDate.getMonth() + 1).toString().padStart(2, "0");

  // 예산 데이터 조회
  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      try {
        const response = await Api.get(
          `api/budget/categories?year=${year}&month=${month}`
        );

        const backendData = response.data.data?.categories || [];
        console.log("예산 데이터 조회", backendData);

        // 카테고리 이름 기준으로 매칭하여 예산 구성
        const mappedData = categories.map((cat) => {
          const matched = backendData.find(
            (item) => item.categoryName === cat.name
          );

          return {
            category_id: cat.id,
            category_name: cat.name,
            budget_amount: matched?.amount || 0,
            remaining_amount: matched?.remainingAmount || 0,
            spending_amount: matched?.spendingAmount || 0,
            is_exceed: matched?.isExceed || false,
          };
        });

        setBudgetData(mappedData);
        setIsFirstBudget(backendData.length === 0);

        // 총 예산 계산
        const total = mappedData.reduce(
          (sum, item) => sum + (item.budget_amount || 0),
          0
        );
        setTotalBudget(total);
      } catch (error) {
        console.log("예산 데이터 조회 실패:", error);
        // 에러 시에도 빈 템플릿 제공
        const emptyBudget = categories.map((cat) => ({
          category_id: cat.id,
          category_name: cat.name,
          budget_amount: 0,
          remaining_amount: 0,
          spending_amount: 0,
          is_exceed: 0,
        }));
        setBudgetData(emptyBudget);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [year, month]);

  // 이전 달 예산 불러오기
  const importLastMonthBudget = async () => {
    try {
      // 이전 달 계산
      const prevDate = new Date(activeDate);
      prevDate.setMonth(prevDate.getMonth() - 1);
      const prevYear = prevDate.getFullYear();
      const prevMonth = (prevDate.getMonth() + 1).toString().padStart(2, "0");

      const response = await Api.get(
        `api/budget/categories?year=${prevYear}&month=${prevMonth}`
      );

      if (response.data.data && response.data.data.length > 0) {
        setBudgetData(response.data.data);

        // 총 예산 계산
        const total = response.data.data.reduce(
          (sum, item) => sum + (item.budget_amount || 0),
          0
        );
        setTotalBudget(total);

        alert("이전 달 예산을 불러왔습니다.");
      } else {
        alert("이전 달 예산 데이터가 없습니다.");
      }
    } catch (error) {
      console.log("이전 달 예산 불러오기 실패:", error);
      alert("이전 달 예산을 불러오는데 실패했습니다.");
    }
  };

  // 예산 값 변경 핸들러
  const handleBudgetChange = (categoryId, value) => {
    const numberValue =
      value === "" ? 0 : parseInt(value.replace(/,/g, ""), 10);

    setBudgetData((prev) =>
      prev.map((item) =>
        item.category_id === categoryId
          ? { ...item, budget_amount: numberValue }
          : item
      )
    );

    // 총 예산 업데이트
    const updatedTotal = budgetData
      .map((item) =>
        item.category_id === categoryId ? numberValue : item.budget_amount
      )
      .reduce((sum, amount) => sum + amount, 0);

    setTotalBudget(updatedTotal);
  };

  // 예산 저장
  // const saveBudget = async (category_id, amount) => {
  //   try {
  //     // 백엔드에서 기대하는 형식으로 변환

  //     const payload = {
  //       category_id
  //       amount,
  //     };
  //     console.log()
  //     await Api.post(`api/budget/plan?year=${year}&month=${month}`, payload);
  //     alert("예산이 성공적으로 저장되었습니다.");
  //   } catch (error) {
  //     console.error("예산 저장 실패:", error);
  //     alert("예산 저장에 실패했습니다.");
  //   }
  // };

  // 숫자에 천 단위 콤마 추가
  const formatNumber = (num) => {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  };

  return (
    <div className="pb-24">
      <Container>
        <CustomBackHeader title="예산 설정" />
        <div className="w-full bg-blue-100 rounded-lg shadow-sm p-4 mb-4">
          <p className="text-xl font-semibold text-blue-800">
            예산 설정으로 돈 모으기
          </p>
          <p className="text-lg font-bold text-blue-900 mt-2">
            총 예산: {formatNumber(totalBudget)}원
          </p>
        </div>

        {/* 월 선택 */}
        <div className="w-full bg-white rounded-lg shadow-sm mb-4">
          <MonthBar
            activeDate={activeDate}
            setActiveDate={setActiveDate}
            onYearMonthChange={({ year, month }) => {
              console.log("선택된 연/월:", year, month);
            }}
          />
        </div>

        {isLoading ? (
          <div className="text-center py-8">로딩 중...</div>
        ) : (
          <>
            {/* 예산 처음 생성 시 안내 메시지 */}
            {isFirstBudget && (
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
                <p className="text-sm text-yellow-700">
                  첫 예산 설정이네요! 각 카테고리별로 예산을 설정해 보세요.
                </p>
                <button
                  onClick={importLastMonthBudget}
                  className="mt-2 text-sm bg-yellow-100 px-4 py-2 rounded-md text-yellow-800 hover:bg-yellow-200"
                >
                  이전 달 예산 불러오기
                </button>
              </div>
            )}

            {/* 카테고리별 예산 설정 */}
            <div className="w-full bg-white rounded-lg shadow-sm p-4">
              <h3 className="text-lg font-semibold mb-4">
                카테고리별 예산 설정
              </h3>

              <div className="space-y-4">
                {budgetData.map((item) => {
                  // 카테고리 정보 찾기
                  const categoryInfo = categories.find(
                    (cat) => cat.id === item.category_id
                  );

                  return (
                    <div
                      key={item.category_id}
                      className="flex items-center justify-between border-b pb-3"
                    >
                      <div className="flex items-center">
                        {categoryInfo && (
                          <div className="w-10 h-10 mr-3">
                            <img
                              src={categoryInfo.Icon}
                              alt={item.category_name}
                              width={40}
                              height={40}
                            />
                          </div>
                        )}
                        <span className="text-gray-800">
                          {item.category_name}
                        </span>
                      </div>

                      <div className="flex flex-col items-end">
                        <div className="flex items-center mb-1">
                          <input
                            type="text"
                            className="w-28 text-right border rounded-md px-2 py-1"
                            value={
                              item.budget_amount
                                ? formatNumber(item.budget_amount)
                                : ""
                            }
                            onChange={(e) =>
                              handleBudgetChange(
                                item.category_id,
                                e.target.value
                              )
                            }
                            placeholder="0"
                          />
                          <span className="ml-1">원</span>
                        </div>

                        {/* 지출 정보 표시 (데이터가 있을 경우만) */}
                        {item.spending_amount !== undefined && (
                          <div className="text-xs">
                            <span
                              className={
                                item.is_exceed
                                  ? "text-red-500"
                                  : "text-gray-500"
                              }
                            >
                              지출: {formatNumber(item.spending_amount)}원
                              {item.is_exceed ? " (초과)" : ""}
                            </span>
                          </div>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* 저장 버튼 */}
            <div className="bottom-0 left-0 right-0 bg-black p-4 shadow-lg">
              <button
                onClick={saveBudget}
                className="w-full bg-blue-500 text-white py-3 rounded-lg font-medium hover:bg-blue-600"
              >
                예산 저장하기
              </button>
            </div>
          </>
        )}
      </Container>
    </div>
  );
}
