import { useState } from "react";

import AllIcon from "../assets/icons/AllIcon.png";
import AllIconInactive from "../assets/icons/AllIconInactive.png";
import EntertainmentIcon from "../assets/icons/EntertainmentIcon.png";
import EntertainmentIconInactive from "../assets/icons/EntertainmentIconInactive.png";
import FoodIcon from "../assets/icons/FoodIcon.png";
import FoodIconInactive from "../assets/icons/FoodIconInactive.png";
import GeneralGoodsIcon from "../assets/icons/GeneralGoodsIcon.png";
import GeneralGoodsIconInactive from "../assets/icons/GeneralGoodsIconInactive.png";
import HousingIcon from "../assets/icons/HousingIcon.png";
import HousingIconInactive from "../assets/icons/HousingIconInactive.png";
import MedicalIcon from "../assets/icons/MedicalIcon.png";
import MedicalIconInactive from "../assets/icons/MedicalIconInactive.png";
import ShoppingIcon from "../assets/icons/ShoppingIcon.png";
import ShoppingIconInactive from "../assets/icons/ShoppingIconInactive.png";
import TransportationIcon from "../assets/icons/TransportationIcon.png";
import TransportationIconInactive from "../assets/icons/TransportationIconInactive.png";
import IncomeIcon from "../assets/icons/IncomeIcon.png";
import IncomeIconInactive from "../assets/icons/IncomeIconInactive.png";
import SpenseIcon from "../assets/icons/SpenseIcon.png";
import SpenseIconInactive from "../assets/icons/SpenseIconInactive.png";

const categories = [
  { id: "all", activeIcon: AllIcon, inactiveIcon: AllIconInactive },
  { id: "food", activeIcon: FoodIcon, inactiveIcon: FoodIconInactive },
  { id: "housing", activeIcon: HousingIcon, inactiveIcon: HousingIconInactive },
  {
    id: "entertainment",
    activeIcon: EntertainmentIcon,
    inactiveIcon: EntertainmentIconInactive,
  },

  {
    id: "goods",
    activeIcon: GeneralGoodsIcon,
    inactiveIcon: GeneralGoodsIconInactive,
  },

  { id: "medical", activeIcon: MedicalIcon, inactiveIcon: MedicalIconInactive },
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
    id: "income",
    activeIcon: IncomeIcon,
    inactiveIcon: IncomeIconInactive,
  },
  {
    id: "spense",
    activeIcon: SpenseIcon,
    inactiveIcon: SpenseIconInactive,
  },
];

export default function CategoryBox() {
  const [activeCategory, setActiveCategory] = useState("all");

  return (
    <div className="w-full bg-white rounded-lg shadow-sm p-4 flex items-center overflow-x-auto">
      <div className="flex gap-4 snap-x snap-mandatory overflow-x-auto">
        {categories.map((category) => (
          <img
            key={category.id}
            src={
              activeCategory === category.id
                ? category.activeIcon
                : category.inactiveIcon
            }
            alt={category.id}
            className="w-10 h-10 cursor-pointer snap-center"
            onClick={() => setActiveCategory(category.id)}
          />
        ))}
      </div>
    </div>
  );
}
