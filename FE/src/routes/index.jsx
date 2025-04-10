import { BrowserRouter, Routes, Route, Navigate, useLocation } from "react-router-dom";
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

export default function Router() {
  const {isAuthenticated} = useSelector((state)=> state.user)
  const location = useLocation()

  const hideNavBarPaths = ["/","/user","user/login","/user/register"]
  const shouldShowNavBar = !hideNavBarPaths.includes(location.pathname)

  return (
    <>
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
      {shouldShowNavBar && <NavBar />}
    </>
  );
}
