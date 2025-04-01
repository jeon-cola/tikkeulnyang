import { useEffect, useState } from "react"
import { Link, useNavigate } from "react-router-dom";
import { resetUser, setDeposit } from "../../user/UserSlice.js"
import userProfile from "/userProfile.png"
import CorrectionIcon from "../assets/CorrectionIcon";
import MyCatIcon from "../assets/MyCatIcon";
import AccountIcon from "../assets/AccountIcon";
import CardIcon from "../assets/CardIcon";
import "../../../components/CustomHeader"
import CustomHeader from "../../../components/CustomHeader";
import YellowCat from "../../../../public/YellowCat.jsx";
import AttendanceIcon from "../assets/AttendanceIcon.jsx";
import LogoutIcon from "../assets/LogoutIcon.jsx";
import { useDispatch, useSelector } from "react-redux";
import Api from "../../../services/Api.jsx";
import MoreIcon from "../assets/MoreIcon.jsx";
import CustomModal from "../../../components/CustomModal.jsx";
import ChooseAlertModal from "../../../components/ChooseAlertModal.jsx";
import AlertModal from "../../../components/AlertModal.jsx";

export default function MyPageMenu() {
  const dispatch = useDispatch();
  const {nickName, email,profileImg,deposit } = useSelector(state => state.user);
  const [pendingChallenges, setPendingChallenges] = useState(0);
  const [activeChallenges, setActiveChallenges] = useState(0);
  const [completedChallenges, setCompletedChallenges] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [buttonChange, setButtonChange]=useState(false)
  const [inputChange, setInputChange] =useState("")
  const [isChooseModal, setIsChooseModal] =useState(false)
  const [isShowModal, setIsShowModal] = useState(false)
  const nav = useNavigate();

  
  // 예치금 환불
  function refundHandler() {
    const fetchData = async ()=> {
      try {
        console.log(inputChange)
        const response = await Api.post("/api/account/refund-deposit",{
          "amount":inputChange
        })
        console.log(response)
        if (response.data === "예치금 환불 요청이 완료되었습니다.") {
          window.alert("예치금 환불이 완료되었습니다")
          const currentDeposit = parseInt(deposit,10)
          const additionAmount = parseInt(inputChange,10)
          const newDeposit = (currentDeposit-additionAmount).toString()
          dispatch(setDeposit(newDeposit))
        } else if (response.data === "환불 요청 금액이 예치금보다 많습니다.") {
          window.alert("환불 요청 금액이 예치금보다 많습니다")
        }
      } catch (error) {
        console.log(error)
      }
    }
    fetchData()
  }
  
  // 예치금 충전 
  function chargeHandler( ) {
    const fetchData = async () => {
      console.log(inputChange)
      try {
        const response = await Api.post("/api/account/deposit-charge",{
          "amount":inputChange
        })
        console.log(response.data)
        if (response.data === "예치금 충전 요청이 완료되었습니다.") {
          window.alert("예치금 충전이 완료되었습니다")
          const currentDeposit = parseInt(deposit,10)
          const additionAmount = parseInt(inputChange,10)
          const newDeposit = (currentDeposit+additionAmount).toString()
          dispatch(setDeposit(newDeposit))
        } else if (response.data === "계좌잔액이 부족합니다.") {
          window.alert("계좌잔액이 부족합니다")
        }
      } catch (error) {
        console.log(error)
      }
    }
    fetchData()
  }
  
  // 입력값 변환환
  function inputHandler(e) {
    const value = e.target.value
    setInputChange(value)
  }
  
  // 모달 열기기
  function modalHandler () {
    setIsModalOpen(true)
  }
  
  // 모달 닫기기
  function onCloseModal() {
    setIsModalOpen(false)
  }
  
  
  // 챌린지 참여 이력
  useEffect(() => {
    const fetchChallengeHistory  = async() => {
      try {
        const response = await Api.get("/api/challenge/past");
        setCompletedChallenges(response.data.length)
      } catch (error) {
        console.log(error);
      }
    }
    fetchChallengeHistory ();
  },[]);
  
  // 진행 중인 챌린지 
  useEffect(()=> {
    const fetchActiveChallenge = async () => {
      try {
        const response = await Api.get("api/challenge/participated")
        setActiveChallenges(response.data.length)
      } catch (error) {
        console.log(error)
      }
    }
    fetchActiveChallenge()
  })

  // 로그아웃 모달 열기
  function isChooseModaOpenlHandler() {
    setIsChooseModal(true)
  }

  // 로그아웃 모달 닫기기
  function isChooseModalCloseHandler() {
    setIsChooseModal(false)
  }

  // 로그인 확인
  function closeIsShowModalHandler() {
    setIsShowModal(false)
    nav("/home")
  }
  
  // 로그아웃 핸들러
  async function logoutHandler() {
    try {
      const response = await Api.post("api/auth/logout")
        if (response.data.status === "success") {
          dispatch(resetUser())
          setIsShowModal(true)
        }
      } catch (error) {
        console.log(error)
        window.alert("에러: 잠시 후 시도해주세요")
      }
  }

  // 충전으로 전환
  function chargeModeHandler() {
    setButtonChange(false)
  }

  //환불으로 전환
  function refundModeHandler() {
    setButtonChange(true)
  }

  return(
      <div className="flex flex-col justify-center gap-5 min-w-[345px] mt-[50px] mb-[30px]">

        <CustomHeader title="마이 페이지"/>
        
        {/* 유저 정보 */}
        <div className="w-full t flex items-center p-4 relative bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px]">
          <div className="mr-4">
            <img src={profileImg} alt="유저 이미지" className="w-[80px] h-[80px] rounded-full "/>
          </div>
          <div className="flex flex-col">
            <div className="flex items-center mb-1 gap-3">
              <span className="font-semibold">{nickName}</span>
            </div>
            <div>
              <span className="font-regular text-x">{email}</span>
            </div>
          </div>
        </div>

          {/* 예치금 */}
          <div className="relative">
            <p className="text-left font-semibold">예치금</p>
            
            {/* 박스 */}
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 mt-2 relative" onClick={modalHandler}>
              <div className="flex items-center justify-center mx-auto">
                <p className="text-xl font-semibold">총 금액 :</p>
                <p className="text-xl font-semibold ml-2">{deposit}원</p>
              </div>
              
              <div className="absolute right-4">
                <MoreIcon />
              </div>
            </div>
            <CustomModal title="예치금" isOpen={isModalOpen} onClose={onCloseModal} >
              <div className="flex flex-col gap-3">

                <div className="w-full flex">
                  <button onClick={chargeModeHandler} className={!buttonChange ? "w-full smallButton" : "w-full smallWhiteButton"}>충천</button>
                  <button onClick={refundModeHandler} className={buttonChange ? "w-full smallButton" : "w-full smallWhiteButton"}>환불</button>
                </div>

                <div className="w-full flex">
                  {(!buttonChange)
                  ?<div className="flex flex-col gap-4 m-1 items-center w-full">
                    
                    <div className="w-full">
                      <p className="font-thin">예치금 충전은 대표계좌에서</p>
                      <p className="font-thin">자동으로 충전됩니다</p>
                    </div>
                    
                    <div className="flex w-full justify-center">
                      <p className="font-semibold text-xl">예치금 잔액 :</p>
                      <p className="font-semibold text-xl">{deposit}</p>
                    </div>
                    
                    <div className="w-full flex flex-col gap-3">
                      <p className="font-semibold text-xl w-full">충전할 금액 :</p>
                      <input type="text" placeholder="충전할 금액을 입력해주세요" className="w-full shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] bg-gray-200 p-2" value={inputChange} onChange={inputHandler}/>
                    </div>
                    
                    <div>
                      <button className="customButton" onClick={chargeHandler}>충전하기</button>
                    </div>
                    
                  </div> 
                  : <div className="flex flex-col gap-4 m-1 items-center w-full">
                      <div className="w-full">
                        <p className="font-thin">예치금 환불은 대표계좌에</p>
                        <p className="font-thin">자동으로 환불됩니다</p>
                      </div>
                      <div className="flex w-full justify-center">
                        <p className="font-semibold text-xl">예치금 잔액 :</p>
                        <p className="font-semibold text-xl">{deposit}</p>
                    </div>

                    <div className="w-full flex flex-col gap-3">
                      <p className="font-semibold text-xl w-full">환불할 금액 :</p>
                      <input type="text" placeholder="환불할 금액을 입력해주세요" className="w-full shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] bg-gray-200 p-2" value={inputChange} onChange={inputHandler}/>
                    </div>
                    
                    <div>
                      <button className="customButton" onClick={refundHandler}>환불하기</button>
                    </div>

                    </div>}
                </div>
              </div>
            </CustomModal>
        </div>

        {/* 챌린지 현황 */}
        <div className="relative">
          <p className="text-left font-semibold">첼린지 현황</p>
          
          {/* 박스 */}
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[25px] flex justify-between items-center p-4 mt-2">
            <div className="flex flex-col items-center w-1/3">
              <p className="text-xl font-semibold">{pendingChallenges}</p>
              <p className="text-sm text-gray-500">시작 전</p>
            </div>

            <div className="flex flex-col items-center w-1/3">
              <p className="text-xl font-semibold text-red-500">{activeChallenges}</p>
              <p className="text-sm text-gray-500 text-red-500">진행 중</p>
            </div>

            <div className="flex flex-col items-center w-1/3">
              <p className="text-xl font-semibold">{completedChallenges}</p>
              <p className="text-sm text-gray-500">완료</p>
            </div>
          </div>
          
          {/* 오른쪽 위에 고정된 YellowCat */}
          <div className="absolute -top-4 right-4">
            <YellowCat/>
          </div>
        </div>

        <div className="flex flex-col gap-4">

        <p className="text-left font-semibold">마이 메뉴</p>

          {/* 회원정보 수정 */}
          <Link to="correction">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
              <div className="mr-4">
                <CorrectionIcon/>
              </div>
                <div className="flex items-center mb-1 gap-3">
                  <p className="font-regular">회원정보 수정</p>
                </div>
            </div>
          </Link>

          {/* 내 고양이
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
            <div className="mr-4">
                <MyCatIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">내 고양이</p>
              </div>
          </div> */}

          {/* 등록 계좌 설정 */}
          <Link to="account">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
              <div className="mr-4">
                <AccountIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">대표 계좌 설정</p>
              </div>
            </div>
          </Link>

          {/* 등록 카드 설정 */}
          <Link to="card">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
              <div className="mr-4">
                <CardIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">등록 카드 설정</p>
              </div>
            </div>
          </Link>

          {/* 출석부 리포트
          <Link to="attendance">
            <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 flex items-center p-4">
              <div className="mr-4">
                <AttendanceIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">출석부 리포트</p>
              </div>
            </div>
          </Link> */}

          {/* 로그아웃 */}
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 flex items-center p-4" onClick={isChooseModaOpenlHandler}>
              <div className="mr-4">
                <LogoutIcon/>
              </div>
              <div className="flex items-center mb-1 gap-3">
                <p className="font-regular">로그아웃</p>
              </div>
            </div>

          {/* 로그아웃 모달 */}
          <ChooseAlertModal title="로그아웃" height={200} isClose={isChooseModalCloseHandler} isOpen={isChooseModal} isFunctionHandler={logoutHandler} >
            <div>
              <p>로그아웃 하시겠습니까?</p>
            </div>
          </ChooseAlertModal>
          
          {/* 로그아웃 확인 */}
            <AlertModal title="로그아웃" height={170} isClose={closeIsShowModalHandler} isOpen={isShowModal}>
              <div>
                <p className="text-xl">로그아웃 되었습니다</p>
              </div>
            </AlertModal>

        </div>

      </div>

      

  )
}