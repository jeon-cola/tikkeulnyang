import { useEffect } from "react";
import { useSelector } from "react-redux";
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
