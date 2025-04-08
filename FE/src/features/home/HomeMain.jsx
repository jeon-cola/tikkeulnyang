import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import Api from "../../services/Api";
import { setEmail, setNickName, setProfileImg } from "../user/UserSlice";
import axios from "axios";
import { Route, Routes, useNavigate } from "react-router-dom";
import SubScribe from "./components/SubScribe";
import RenderHome from "@/features/home/RenderHome";

export default function HomeMain() {
  const nav = useNavigate()
  const {isAuthenticated} = useSelector((state)=> state.user)
  useEffect(()=>{
    if (!isAuthenticated) {
      nav("/user")
    }
  })
  if (!isAuthenticated) return null
  return (
    <Routes>
      <Route path="/" element={<RenderHome />} />
      <Route path="subscribe" element={<SubScribe />} />
    </Routes>
  );
}
