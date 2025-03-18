import { Route, Routes } from "react-router-dom";
import MyPageMenu from "./components/MyPageMenu";

export default function MyPageMain(){
    return(
        <Routes>
            <Route path="/" element={<MyPageMenu/>}/>
        </Routes>
    )
}