import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import Api from "../../services/Api";
import { setEmail, setNickName, setProfileImg } from "../user/UserSlice";
import axios from "axios";
import { Route, Routes } from "react-router-dom";
import SubScribe from "./components/SubScribe";

export default function HomeMain() {
  return (
    <Routes>
      <Route path="/"/>
      <Route path="subscribe" element={<SubScribe/>}/>
    </Routes>
  );
}
