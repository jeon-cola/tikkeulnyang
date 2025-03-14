import { Route, Routes } from "react-router-dom";
import Login from "./components/Login";

export default function UserMain() {
    return (
        <Routes>
            <Route path="/" element={<Login/>}/>
        </Routes>
    )
}