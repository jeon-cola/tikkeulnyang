import { Route, Routes } from "react-router-dom";
import HomeMain from "../features/home/HomeMain";

export default function HomeRouter() {
    return (
        <Routes>
            <Route path="/" element={<HomeMain/>}/>
        </Routes>
    )
}