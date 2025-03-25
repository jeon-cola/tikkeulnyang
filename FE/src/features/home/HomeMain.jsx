import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import Api from "../../services/Api"
import { setEmail, setNickName } from "../user/UserSlice"
import axios from "axios";

export default function HomeMain() {
    const dispatch = useDispatch();
    const {nickName, email} = useSelector(state => state.user)
    useEffect(()=> {
        const fetchData = async ()=> {
            try {
                const response = await axios.get("http://localhost:8080/api/user/me")
                if (response.status === "success") {
                    dispatch(setEmail(response.data.email))
                    console.log("email",response.data.email)
                    dispatch(setNickName(response.data.nickname))
                    console.log("nickname",response.data.nickname)
                }
            } catch (error) {
                console.log(error)
            }
        }
        fetchData();
    },[])
    return (
        <>
<<<<<<< HEAD
            {email}
            {nickName}
=======
            <button className="longButton"></button>
>>>>>>> f56c715cb2b809e933d71b32b678eaec47ee6b14
        </>
    )
}