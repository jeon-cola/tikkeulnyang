import { useEffect, useState } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import baseImg from "/userProfile.png"
import { useSelector } from "react-redux"
import axios from "axios"
import Api from "../../../services/Api"

export default function Correction() {
    const [userImg, setUserImg] = useState(baseImg)
    const [userNickName, setUserNcikName] = useState("의적");
    const [userName, setUserName] = useState("길동홍");
    const [userEmail, setUserEmail] = useState("abc@naver.com")
    const [userBirth, setUserBirth] = useState("2006.01.01")
    const [correctionCheck, setCorrectionCheck] = useState(false)
    
    useEffect(()=> {
        const fetchData = async () => {
                try {
                // const response = await axios.get("http://localhost:8080/api/user",{
                //     withCredentials:true
                // })
                const response = await Api.get("api/user")
                console.log(response.data)
                if (response.data.status === "success") {
                    const userData = response.data
                    setUserName(userData.name)
                    setUserNcikName(userData.nickname)
                    setUserEmail(userData.email)
                    setUserBirth(userData.birthDate)
                }
            } catch (error) {
                console.log(error)
                window.alert("서버에러 잠시후 시도해 주세요")
        } 
        }
        fetchData()
    },[])

    function CorrectionHandler(e){
        e.preventDefault();
        setCorrectionCheck(!correctionCheck)
    };

    return (
        <div className="flex flex-col gap-7 min-w-[345px]">
            <CustomBackHeader title="회원정보 수정" />
            <div className="w-full px-4 mt-16">
                <p className="font-medium mb-2">프로필</p>
                <div className="relative w-24 h-24 mx-auto">
                    {/* 프로필 이미지 */}
                    <div className="w-full h-full rounded-full bg-gray-100 flex items-center justify-center overflow-hidden">
                        <img src={userImg} alt="프로필 이미지" className="w-full h-full object-cover "/>
                    </div>
                    
                    {/* 카메라 아이콘 */}
                    <div className="absolute bottom-0 right-0  rounded-full p-2">
                        <img src="/camera.png" alt="카메라" className="w-7 h-7" />
                    </div>
                </div>
            </div>

                {/* 유저 정보 창 */}
                <div className="w-full flex flex-col gap-6">
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 justify-between ">
                        <p >닉네임</p>
                        <input type="text" placeholder={userNickName} readOnly={!correctionCheck} />
                    </div>
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 justify-between ">
                        <p >이름</p>
                        <input type="text" placeholder={userName} readOnly={!correctionCheck} />
                    </div>
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 justify-between ">
                        <p >생년월일</p>
                        <div className="w-[177px]">
                            <p className="text-left">{userBirth}</p>
                        </div>
                    </div>
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 justify-between ">
                        <p >대표 이메일</p>
                        <div className="w-[177px]">
                            <p className="text-left">{userEmail}</p>
                        </div>
                    </div>
                </div>
                <div className="w-full flex justify-center">
                    <button onClick={CorrectionHandler}>{!correctionCheck? "수정하기" : "저장하기"}</button>
                </div>
        </div>
    )
}