import { Route, Routes } from "react-router-dom";
import MyPageMenu from "./components/MyPageMenu";
import Correction from "./components/Correction";
import Account from "./components/Account";
import Card from "./components/Card";

export default function MyPageMain(){
    return(
        <Routes>
            <Route path="/" element={<MyPageMenu/>}/>
            <Route path="correction" element={<Correction/>}/>
            <Route path="account" element={<Account/>}/>
            <Route path="card" element={<Card/>}/>
            <Route path="attendance" element={<Attendance/>}/>
        </Routes>
    )
}
