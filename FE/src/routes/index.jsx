import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import NavBar from "../components/navBar";
import LedgerRouter from "./LedgerRouter";
import ChallengeMainRouter from "./ChallengeMainRouter";
import BucketListRouter from "./BucketListRouter";
import MyPageRouter from "./MyPageRouter";
import HomeMain from "../features/home/HomeMain";
import CardRouter from "./CardRouter";
import UserRouter from "./UserRouter";


export default function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/home/*" element={<HomeMain/>}/>
        <Route path="/bucketlist/*" element={<BucketListRouter/>}/>
        <Route path="/challenge/*" element={<ChallengeMainRouter/>}/>
        <Route path="/ledger/*" element={<LedgerRouter/>}/>
        <Route path="/mypage/*" element={<MyPageRouter/>}/>
        <Route path="/card/*" element={<CardRouter/>} />
        <Route path="/user/" element={<UserRouter/>} />
      </Routes>

      {/* 하단 앱 바 */}
      <NavBar/>
    </BrowserRouter>
  );
}
