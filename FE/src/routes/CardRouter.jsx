import { Route, Routes } from "react-router-dom";
import CardMain from "../features/card/CardMain";

export default function CardRouter() {
    return(
        <Routes>
            <Route path="/" element={<CardMain/>}/>
        </Routes>
    )
}