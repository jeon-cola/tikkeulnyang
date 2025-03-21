import { useState } from "react"
import City from "../../../assets/bank/City"
import Daegu from "../../../assets/bank/Daegu"
import Gwangju from "../../../assets/bank/Gwangju"
import Hana from "../../../assets/bank/Hana"
import Ibk from "../../../assets/bank/Ibk"
import Jeju from "../../../assets/bank/Jeju"
import Jeonbuk from "../../../assets/bank/jeonbuk"
import Kakao from "../../../assets/bank/Kakao"
import Kdb from "../../../assets/bank/Kdb"
import Kb from "../../../assets/bank/Kb"
import Keb from "../../../assets/bank/Keb"
import Kyungnam from "../../../assets/bank/Kyungnam"
import Mg from "../../../assets/bank/Mg"
import Nh from "../../../assets/bank/Nh"
import Sc from "../../../assets/bank/Sc"
import Shinhan from "../../../assets/bank/Shinhan"
import Woori from "../../../assets/bank/Woori"

export default function BankImg({bankName}) {
    if (bankName==="산업은행") {
        return Kdb
    } else if (bankName === "기업은행") {
        return Ibk
    } else if (bankName === "국민은행") {
        return Kb
    } else if (bankName === "농협은행") {
        return Nh
    } else if (bankName === "우리은행") {
        return Woori
    } else if (bankName === "sc제일은행") {
        return Sc
    } else if (bankName === "시티은행") {
        return City
    } else if (bankName === "대구은행") {
        return Daegu
    } else if (bankName === "광주은행") {
        return Gwangju
    } else if (bankName === "제주은행") {
        return Jeju
    } else if (bankName === "전북은행") {
        return Jeonbuk
    } else if (bankName === "경남은행") {
        return Kyungnam
    } else if (bankName === "새마을금고") {
        return Mg
    } else if (bankName === "하나은행") {
        return Keb
    } else if (bankName === "신한은행") {
        return Shinhan
    } else if (bankName === "카카오뱅크") {
        return Kakao
    } 
}