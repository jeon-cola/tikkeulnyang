import { Route, Routes } from "react-router-dom";
import UserMain from "../features/user/UserMain";

export default function UserRouter() {
    return (
        <Routes>
            <Route path="/" element={<UserMain/>}/>
        </Routes>
    )
}