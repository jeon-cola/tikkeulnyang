import { Route, Routes } from "react-router-dom";
import MyPageMain from "../features/mypage/MyPageMain";

export default function MyPageRouter() {
    return(
        <Routes>
            <Route path="/*" element={<MyPageMain/>}/>
        </Routes>
    )
}