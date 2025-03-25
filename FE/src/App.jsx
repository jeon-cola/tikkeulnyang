import "./App.css";
import Router from "./routes";
import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import { setEmail, setNickName, setProfileImg } from "./features/user/UserSlice.js"
import axios from "axios";
import Api from "./services/Api";

function App() {
  const dispatch = useDispatch();
  const {nickName, email, profileImg} = useSelector(state => state.user)
  useEffect(()=> {
      const fetchData = async ()=> {
          try {
              // const response = await Api.get("/api/user/me")
              const response = await axios.get("http://localhost:8080/api/user/me",{
                  withCredentials:true
              })
              if (response.data.body.status === "success") {
                  const userData = response.data.body.data
                  dispatch(setEmail(userData.email))
                  dispatch(setNickName(userData.nickname))
              }
          } catch (error) {
              console.log(error)
          }
      }
      fetchData();
  },[dispatch])
  return (
    <>
      <Router />
    </>
  );
}

export default App;
