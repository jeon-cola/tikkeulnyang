import { Route, Routes } from "react-router-dom";
import MyPageMenu from "./components/MyPageMenu";
import Correction from "./components/Correction";

export default function MyPageMain(){
    return(
        <Routes>
            <Route path="/" element={<MyPageMenu/>}/>
            <Route path="correction" element={<Correction/>}/>
        </Routes>
    )
}