import { useEffect, useRef, useState } from "react"
import CustomBackHeader from "../../../components/CustomBackHeader"
import { useDispatch, useSelector } from "react-redux"
import Api from "../../../services/Api"
import CustomModal from "../../../components/CustomModal"
import { useNavigate } from "react-router-dom"
import {setProfileImg,setNickName, resetUser } from "../../user/UserSlice"
import AlertModal from "../../../components/AlertModal"

export default function Correction() {
    const [userImg, setUserImg] = useState("")
    const [userNickName, setUserNcikName] = useState("의적");
    const [userName, setUserName] = useState("길동홍");
    const [userEmail, setUserEmail] = useState("abc@naver.com")
    const [userBirth, setUserBirth] = useState("2006.01.01")

    const [correctionCheck, setCorrectionCheck] = useState(false)
    const [isNickName, setIsNickName] = useState(true)
    const [isModalOpen, setIsModalOpen] = useState(false)
    const [isLeaveModal, setIsLeaveModal] =useState(false)
    const [message, setMessage] = useState("")
    const [isOpen, setIsOpen] = useState(false)
    const [fileImg, setFileImg] = useState(null);
    const fileInput = useRef(null);
    const nav =useNavigate()
    const dispatch = useDispatch()

     function closeHandler(){
        setIsOpen(false)
     }

    function handleButtonClick() {
        fileInput.current.click()
    }

    function handleFileChnage(e) {
        const file = e.target.files[0]
        if (file) {
            const reader = new FileReader()
            reader.onload = (e)=> {
                setFileImg(e.target.result)
                setIsModalOpen(true)
            }
            reader.readAsDataURL(file)
            console.log("file",file)
        }
    }

    // 이미지 변경
    function fileUpdateHandler() {
        try {
            const formData = new FormData()
            const file = fileInput.current.files[0]
            if (!file) return null
    
            formData.append("file",file)
    
            const fetchData = async () => {
                const response = await Api.post("api/user/profile-image",formData)
                console.log(response)
                if (response.data.status === "success") {
                    setMessage("이미지 변경에 성공하셨습니다")
                    setIsOpen(true)
                    dispatch(setProfileImg(response.data.data))
                    setUserImg(response.data.data)
                    setIsModalOpen(false)
                } else {
                    setIsOpen(true)
                    setMessage("인증 오류. 다시 로그인 해주시기 바랍니다")
                    nav("/user")
                }
            }
            fetchData()
        } catch (error) {
            console.log(error)
            setMessage("서버 에러. 다시 시도해주시기 바랍니다")
            setIsOpen(true)
        }
    }
    
    // 모달 닫기
    function closeModal() {
        setIsModalOpen(false)
    }
    
    function leaveCloseModal() {
        setIsLeaveModal(false)
    }

    // 회원정보 조회
    useEffect(()=> {
        const fetchData = async () => {
                try {
                const response = await Api.get("api/user/info")
                console.log("회원 정보 조회",response.data.data)
                if (response.data.status === "success") {
                    const userData = response.data.data
                    setUserImg(userData.profileImage)
                    setUserName(userData.name)
                    setUserNcikName(userData.nickname)
                    setUserEmail(userData.email)
                    setUserBirth(userData.birthDate)
                }
            } catch (error) {
                console.log(error)
                setMessage("서버에러 잠시후 시도해 주세요")
                setIsOpen(true)
        } 
        }
        fetchData()
    },[])

    // 회원 정보 수정
    function CorrectionHandler(e){
        e.preventDefault();
        if (!correctionCheck) {
            setCorrectionCheck(true)
            setIsNickName(true)
        }
        else {
            const fetchData = async () => {
                try {
                    const response = await Api.put("api/user/update", {
                        "name":userName,
                        "nickname": userNickName,
                        "birthDate": userBirth
                    })
                    if (response.data.status === "success") {
                        setCorrectionCheck(false)
                        dispatch(setNickName(response.data.data.nickname))
                        setMessage("변경 성공")
                        setIsOpen(true)
                    }
                } catch (error) {
                    if (error.response && error.response.data.status === "fail") {
                        setIsNickName(false)
                        console.log("확인",isNickName,correctionCheck)
                        setMessage("이미 사용중인 닉네임이 존재합니다")
                        setIsOpen(true)
                    } else {
                        console.log(error)
                        setMessage("오류가 발생했습니다. 다시 시도해 주세요")
                        setIsOpen(true)
                    }
                }
            }
            fetchData();
        }
    };

    // 닉네임 정보
    function nickNameHandler(e) {
        const {value} = e.target
        setUserNcikName(value)
        setIsNickName(true)
    }

    //회원 탈퇴 모달
    function leaveModalHandler() {
        setIsLeaveModal(true)
    }

    // 회원 탈퇴
    function leaveHandler() {
        const fetchData = async () => {
            try {
                const response = await Api.delete("api/user/delete")
                if (response.data.status === "success") {
                    setIsOpen(true)
                    setMessage("탈퇴가 완료되었습니다")
                    setIsLeaveModal(false)
                    dispatch(resetUser())
                    nav("/home")
                }
            } catch (error) {
                console.log(error)
                setIsOpen(true)
                setMessage("서버에러 다시 시도해주세요")
            }
        }
        fetchData()
    }

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
                    
                    <input type="file" ref={fileInput} onChange={handleFileChnage} accept="image/*" style={{display:"none"}} />

                    {/* 카메라 아이콘 */}
                    <div className="absolute bottom-0 right-0  rounded-full p-2">
                        <img src="/camera.png" alt="카메라" className="w-7 h-7"  onClick={handleButtonClick}/>
                        {/* 모달 */}
                        <CustomModal isOpen={isModalOpen} onClose={closeModal} title="이미지 변경">
                            <p className="text-xl">해당 이미지로 변경하시겠습니까?</p>
                            <div className="w-full flex justify-center">
                                <img src={fileImg} alt="이미지" className="w-[200px] h-[200px]"/>
                            </div>
                            <div className="w-full flex justify-center">
                                <button className="customButton" onClick={fileUpdateHandler}>변경하기</button>
                            </div>
                        </CustomModal>
                    </div>
                </div>
            </div>

                {/* 유저 정보 창 */}
                <div className="w-full flex flex-col gap-6">
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 justify-between ">
                        <p >닉네임</p>
                        <div className="flex flex-col">
                            <input type="text" placeholder={userNickName} readOnly={!correctionCheck} value={userNickName} onChange={nickNameHandler} className={`${isNickName&&correctionCheck ? "border-[#ff957a] border":(correctionCheck && !isNickName)? "border-red-500 border" : ""}`}/>
                            {(correctionCheck && !isNickName)?<p className="text-red-500">이미 존재하는 닉네임 입니다</p> : ""}
                        </div>
                    </div>
                    <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 justify-between ">
                        <p >이름</p>
                        <div className="w-[177px]">
                            <p className="text-left">{userName}</p>
                        </div>
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
                    <button className="customButton" onClick={CorrectionHandler}>{!correctionCheck? "수정하기" : "저장하기"}</button>
                </div>

                <div className="absolute bottom-20 right-[20px]">
                  <p className="font-thin text-sm text-gray-500" onClick={leaveModalHandler}>회원 탈퇴</p>
                  <CustomModal isOpen={isLeaveModal} onClose={leaveCloseModal} title="탈퇴하기" className="flex flex-col gap-4">
                    <p>정말 떠나신다니 아쉽습니다 😢</p>
                    <div>
                        <p>한 달만 더 머물러 주셨다면</p>
                        <p>당신의 소비 습관이 얼마나 변화할 수 있는지 보여드리고 싶었어요.</p>
                    </div>
                    <div>
                        <p>지난 사용자들은 평균적으로 <span className="text-xl text-[#ff957a] font-semibold">3개월 챌린지</span>를 통해</p>
                        <p>월 지출의 <span className="text-xl text-[#ff957a] font-semibold">15%를 절약</span>하는데 성공했습니다.</p>
                    </div>
                    <div>
                        <p>언제든 마음이 바뀌시면 다시 돌아와주세요.</p>
                        <p>당신의 현명한 소비 습관을 언제나 응원합니다!</p>
                    </div>
                    <p>(탈퇴 진행을 위해 '탈퇴하기' 버튼을 눌러주세요)</p>
                    <div className="flex justify-center">
                        <button className="customButton" onClick={leaveHandler}>탈퇴하기</button>
                    </div>
                  </CustomModal>
                </div>
                <AlertModal isOpen={isOpen} isClose={closeHandler} height={170}>
                    {message}
                </AlertModal>
        </div>
    )
}