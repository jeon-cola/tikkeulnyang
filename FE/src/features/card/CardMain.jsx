import { Route, Routes } from "react-router-dom";
import CardRecommend from "./components/CardRecommend";
import DetailCard from "./components/DetailCard";

export default function CardMain() {
    return (
        <Routes>
            <Route path="/" element={<CardRecommend/>}/>
            <Route path="detail_card/:cardName" element={<DetailCard/>}/>
        </Routes>
    )
}