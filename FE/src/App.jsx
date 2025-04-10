import "./App.css";
import Router from "./routes";
import { useEffect } from "react"
import { useDispatch } from "react-redux"
import { setEmail, setNickName, setProfileImg, setDeposit, setUserId,setAuthenticated } from "./features/user/UserSlice.js"
import Api from "./services/Api";
import { BrowserRouter } from "react-router-dom";

function App() {
  const dispatch = useDispatch();
  useEffect(()=> {
      const fetchData = async ()=> {
          try {
              const response = await Api.get("/api/user/me")
              if (response.data.body.status === "success") {
                  const userData = response.data.body.data
                  dispatch(setAuthenticated(true))
                  dispatch(setEmail(userData.email))
                  dispatch(setNickName(userData.nickname))
                  dispatch(setProfileImg(userData.profileImage))
                  dispatch(setDeposit(userData.deposit))
                  dispatch(setUserId(userData.id))
              }
          } catch (error) {
              dispatch(setAuthenticated(false))
          }
      }
      fetchData();
  },[dispatch])
  
  return (
    <BrowserRouter>
      <Router />
    </BrowserRouter>
  );
}

export default App;
