import EntertainmentIcon from "@/features/ledger/assets/category/entertainment_icon.png";
import FoodIcon from "@/features/ledger/assets/category/food_icon.png";
import GoodsIcon from "@/features/ledger/assets/category/goods_icon.png";
import HousingIcon from "@/features/ledger/assets/category/housing_icon.png";
import MedicalIcon from "@/features/ledger/assets/category/medical_icon.png";
import ShoppingIcon from "@/features/ledger/assets/category/shopping_icon.png";
import TransportationIcon from "@/features/ledger/assets/category/transportation_icon.png";
import IncomeIcon from "@/features/ledger/assets/category/income_icon.png";
import SpenseIcon from "@/features/ledger/assets/category/spense_icon.png";
import EducationIcon from "@/features/ledger/assets/category/education_icon.png";

export default function CategoryList() {
  return [
    { id: 1, name: "교통/차량", Icon: TransportationIcon },
    { id: 2, name: "쇼핑/미용", Icon: ShoppingIcon },
    { id: 3, name: "교육/육아", Icon: EducationIcon },
    { id: 4, name: "주거/통신", Icon: HousingIcon },
    { id: 5, name: "문화/여가", Icon: EntertainmentIcon },
    { id: 6, name: "병원/약국", Icon: MedicalIcon },
    { id: 7, name: "식비", Icon: FoodIcon },
    { id: 8, name: "잡화", Icon: GoodsIcon },
    { id: 9, name: "결제", Icon: SpenseIcon },
    { id: 10, name: "수입", Icon: IncomeIcon },
  ];
}
