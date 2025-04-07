import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import NavBar from "../components/NavBar";
import LedgerRouter from "./LedgerRouter";
import ChallengeMainRouter from "./ChallengeMainRouter";
import BucketListRouter from "./BucketListRouter";
import MyPageRouter from "./MyPageRouter";
import HomeMain from "../features/home/HomeMain";
import CardRouter from "./CardRouter";
import UserRouter from "./UserRouter";
import AcceptInvite from "@/features/ledger/AcceptInvite";
import { useSelector } from "react-redux";
import Login from "../features/user/components/Login";

export default function Router() {
  const {isAuthenticated} = useSelector((state)=> state.user)
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={isAuthenticated?<Navigate to="/home" replace/> : <UserRouter/>} />
        <Route path="/:token" element={<AcceptInvite />} />
        <Route path="/home/*" element={<HomeMain />} />
        <Route path="/bucketlist/*" element={<BucketListRouter />} />
        <Route path="/challenge/*" element={<ChallengeMainRouter />} />
        <Route path="/ledger/*" element={<LedgerRouter />} />
        <Route path="/mypage/*" element={<MyPageRouter />} />
        <Route path="/card/*" element={<CardRouter />} />
        <Route path="/user/*" element={<UserRouter />} />
      </Routes>

      {/* 하단 앱 바 */}
      <NavBar />
    </BrowserRouter>
  );
}
