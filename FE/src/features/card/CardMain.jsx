import { Route, Routes } from "react-router-dom";
import CardRecommend from "./components/CardRecommend";

export default function CardMain() {
    return (
        <Routes>
            <Route path="/" element={<CardRecommend/>}/>
        </Routes>
    )
}