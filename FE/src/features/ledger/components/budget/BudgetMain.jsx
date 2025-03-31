import Container from "@/components/Container";
import Box from "../Box";
import MonthBar from "../MonthBar";

import EntertainmentIcon from "../../assets/category/entertainment_icon.png";
import FoodIcon from "../../assets/category/food_icon.png";
import GoodsIcon from "../../assets/category/goods_icon.png";
import HousingIcon from "../../assets/category/housing_icon.png";
import MedicalIcon from "../../assets/category/medical_icon.png";
import ShoppingIcon from "../../assets/category/shopping_icon.png";
import TransportationIcon from "../../assets/category/transportation_icon.png";
import IncomeIcon from "../../assets/category/income_icon.png";
import SpenseIcon from "../../assets/category/spense_icon.png";
import WasteIcon from "../../assets/waste_icon.png";
import EmptyIcon from "../../assets/empty_icon.png";

const categories = [
  { id: "식비", Icon: FoodIcon },
  { id: "주거/통신", Icon: HousingIcon },
  { id: "잡화", Icon: GoodsIcon },
  { id: "병원/약국", Icon: MedicalIcon },
  { id: "쇼핑/미용", Icon: ShoppingIcon },
  { id: "교통/차량", Icon: TransportationIcon },
  { id: "문화/여가", Icon: EntertainmentIcon },
  { id: "수입", Icon: IncomeIcon },
  { id: "결제", Icon: SpenseIcon },
];

export default function Bud() {
  return (
    <>
      {/* <MonthBar /> */}
      <Container>
        <Box variants="title">예산설정</Box>
        <div className="w-full h-[100px] bg-white rounded-md shadow-sm px-[10px]"></div>
        <div className="w-full h-[100px] bg-white rounded-md shadow-sm px-[10px]"></div>
      </Container>
    </>
  );
}
