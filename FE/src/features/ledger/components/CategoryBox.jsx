import { useState } from "react";

import AllIcon from "../assets/category/all_icon.png";
import AllIconInactive from "../assets/category/all_icon_inactive.png";
import EntertainmentIcon from "../assets/category/entertainment_icon.png";
import EntertainmentIconInactive from "../assets/category/entertainment_icon_inactive.png";
import FoodIcon from "../assets/category/food_icon.png";
import FoodIconInactive from "../assets/category/food_icon_inactive.png";
import GoodsIcon from "../assets/category/goods_icon.png";
import GoodsIconInactive from "../assets/category/goods_icon_inactive.png";
import HousingIcon from "../assets/category/housing_icon.png";
import HousingIconInactive from "../assets/category/housing_icon_inactive.png";
import MedicalIcon from "../assets/category/medical_icon.png";
import MedicalIconInactive from "../assets/category/medical_icon_inactive.png";
import ShoppingIcon from "../assets/category/shopping_icon.png";
import ShoppingIconInactive from "../assets/category/shopping_icon_inactive.png";
import TransportationIcon from "../assets/category/transportation_icon.png";
import TransportationIconInactive from "../assets/category/transportation_icon_inactive.png";
import EducationIcon from "../assets/category/education_icon.png";
import EducationIconInactive from "../assets/category/education_icon_inactive.png";
import IncomeIcon from "../assets/category/income_icon.png";
import IncomeIconInactive from "../assets/category/income_icon_inactive.png";
import SpenseIcon from "../assets/category/spense_icon.png";
import SpenseIconInactive from "../assets/category/spense_icon_inactive.png";

export default function CategoryBox({ activeCategory, setActiveCategory }) {
  const categories = [
    { id: "all", activeIcon: AllIcon, inactiveIcon: AllIconInactive },
    { id: "food", activeIcon: FoodIcon, inactiveIcon: FoodIconInactive },
    {
      id: "housing",
      activeIcon: HousingIcon,
      inactiveIcon: HousingIconInactive,
    },
    {
      id: "entertainment",
      activeIcon: EntertainmentIcon,
      inactiveIcon: EntertainmentIconInactive,
    },
    { id: "goods", activeIcon: GoodsIcon, inactiveIcon: GoodsIconInactive },
    {
      id: "medical",
      activeIcon: MedicalIcon,
      inactiveIcon: MedicalIconInactive,
    },
    {
      id: "shopping",
      activeIcon: ShoppingIcon,
      inactiveIcon: ShoppingIconInactive,
    },
    {
      id: "transportation",
      activeIcon: TransportationIcon,
      inactiveIcon: TransportationIconInactive,
    },
    {
      id: "education",
      activeIcon: EducationIcon,
      inactiveIcon: EducationIconInactive,
    },
    { id: "income", activeIcon: IncomeIcon, inactiveIcon: IncomeIconInactive },
    { id: "spense", activeIcon: SpenseIcon, inactiveIcon: SpenseIconInactive },
  ];

  return (
    <div className="w-full bg-white rounded-lg shadow-sm p-4 flex items-center overflow-x-auto">
      <div className="flex gap-3 snap-x snap-mandatory overflow-x-auto">
        {categories.map((category) => {
          // "all"인 경우 모든 아이콘을 active 상태로 표시
          const isActive =
            activeCategory === "all" || activeCategory === category.id;
          return (
            <img
              key={category.id}
              src={isActive ? category.activeIcon : category.inactiveIcon}
              alt={category.id}
              className="w-8 h-auto cursor-pointer snap-center"
              onClick={() => setActiveCategory(category.id)}
            />
          );
        })}
      </div>
    </div>
  );
}
