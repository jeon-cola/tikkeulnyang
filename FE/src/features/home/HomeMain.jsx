import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import Api from "../../services/Api"
import { setEmail, setNickName, setProfileImg } from "../user/UserSlice"
import axios from "axios";

export default function HomeMain() {
    const dispatch = useDispatch();
    const {nickName, email, profileImg} = useSelector(state => state.user)
    useEffect(()=> {
        const fetchData = async ()=> {
            try {
                const response = await Api.get("/api/user/me")
                // const response = await axios.get("http://localhost:8080/api/user/me",{
                //     withCredentials:true
                // })
                console.log(response.data)
                if (response.data.body.status === "success") {
                    const userData = response.data.body.data
                    dispatch(setEmail(userData.email))
                    console.log("email",userData.email)
                    dispatch(setNickName(userData.nickname))
                    console.log("nickname",userData.nickname)
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData();
    },[dispatch])
    return (
        <>
        <p>
            {nickName}
        </p>
        <p>
            {email}
        </p>
        </>
    )
}