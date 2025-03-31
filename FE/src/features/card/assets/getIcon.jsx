import TransportIcon from "./TransportIcon.png"
import ShoppingIcon from "./ShoppingIcon.png"
import EducationIcon from "./EducationIcon.png"
import ResidantialIcon from "./ResidantialIcon.png"
import ArtIcon from "./ArtIcon.png"
import HospitalIcon from "./HospitalIcon.png"
import FoodIcon from "./FoodIcon.png"
import HaberdasheryIcon from "./HaberdasheryIcon.png"
import PaymentIcon from "./PaymentIcon.png"

export default function IconFunction(name) {
  console.log(name)
  switch (name) {
    case "교통/차량":
      return TransportIcon
    case "쇼핑/미용":
      return ShoppingIcon
    case "교육/육아":
      return EducationIcon
    case "주거/통신":
      return ResidantialIcon
    case "문화/여가":
      return ArtIcon
    case "병원/약국":
      return HospitalIcon
    case "식비":
      return FoodIcon
    case "잡화":
      return HaberdasheryIcon
    case "결제":
      return PaymentIcon
  
    default:
      return null;
  }
}