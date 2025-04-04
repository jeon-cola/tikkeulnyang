import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { resetUser, setDeposit } from "../../user/UserSlice.js";
import userProfile from "/userProfile.png";
import CorrectionIcon from "../assets/CorrectionIcon";
import MyCatIcon from "../assets/MyCatIcon";
import AccountIcon from "../assets/AccountIcon";
import CardIcon from "../assets/CardIcon";
import "../../../components/CustomHeader";
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
  const { nickName, email, profileImg, deposit } = useSelector(state => state.user);
  const [pendingChallenges, setPendingChallenges] = useState(0);
  const [activeChallenges, setActiveChallenges] = useState(0);
  const [completedChallenges, setCompletedChallenges] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  // mode: "charge", "refund", "history" 탭 전환
  const [mode, setMode] = useState("charge");
  const [inputChange, setInputChange] = useState("");
  const [isChooseModal, setIsChooseModal] = useState(false);
  const [isShowModal, setIsShowModal] = useState(false);
  const [transactions, setTransactions] = useState([]);
  const nav = useNavigate();

  // 예치금 환불
  function refundHandler() {
    const fetchData = async () => {
      try {
        const response = await Api.post("/api/account/refund-deposit", {
          amount: inputChange
        });
        if (response.data === "예치금 환불 요청이 완료되었습니다.") {
          window.alert("예치금 환불이 완료되었습니다");
          const currentDeposit = parseInt(deposit, 10);
          const subtractionAmount = parseInt(inputChange, 10);
          const newDeposit = (currentDeposit - subtractionAmount).toString();
          dispatch(setDeposit(newDeposit));
        } else if (response.data === "환불 요청 금액이 예치금보다 많습니다.") {
          window.alert("환불 요청 금액이 예치금보다 많습니다");
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchData();
  }

  // 예치금 충전
  function chargeHandler() {
    const fetchData = async () => {
      try {
        const response = await Api.post("/api/account/deposit-charge", {
          amount: inputChange
        });
        if (response.data === "예치금 충전 요청이 완료되었습니다.") {
          window.alert("예치금 충전이 완료되었습니다");
          const currentDeposit = parseInt(deposit, 10);
          const additionAmount = parseInt(inputChange, 10);
          const newDeposit = (currentDeposit + additionAmount).toString();
          dispatch(setDeposit(newDeposit));
        } else if (response.data === "계좌잔액이 부족합니다.") {
          window.alert("계좌잔액이 부족합니다");
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchData();
  }

  // 입력값 핸들러
  function inputHandler(e) {
    setInputChange(e.target.value);
  }

  // 모달 열기
  function modalHandler() {
    setIsModalOpen(true);
  }

  // 모달 닫기 (탭 상태, 입력값 초기화)
  function onCloseModal() {
    setIsModalOpen(false);
    setMode("charge");
    setInputChange("");
  }

  // 거래내역 fetch (내역 탭 선택 시)
  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const response = await Api.get("/api/account/service-transactions");
        // response.data가 거래 내역 배열이라 가정
        setTransactions(response.data);
      } catch (error) {
        console.log(error);
      }
    };
    if (mode === "history" && isModalOpen) {
      fetchTransactions();
    }
  }, [mode, isModalOpen]);

  // 거래내역 그룹핑 (날짜별)
  const groupedTransactions = transactions.reduce((groups, transaction) => {
    const date = new Date(transaction.transactionDate).toISOString().split("T")[0];
    if (!groups[date]) groups[date] = [];
    groups[date].push(transaction);
    return groups;
  }, {});

  // 카테고리 매핑
  const categoryMapping = {
    DEPOSIT_CHARGE: "충전",
    DEPOSIT_REFUND: "환불",
    CHALLENGE_JOIN: "챌린지 참여",
    CHALLENGE_DELETE_REFUND: "챌린지 취소",
    CHALLENGE_SETTLE_REFUND: "챌린지 성공"
  };

  // 챌린지 참여 이력
  useEffect(() => {
    const fetchChallengeHistory = async () => {
      try {
        const response = await Api.get("/api/challenge/past");
        setCompletedChallenges(response.data.length);
      } catch (error) {
        console.log(error);
      }
    };
    fetchChallengeHistory();
  }, []);

  // 진행 중인 챌린지
  useEffect(() => {
    const fetchActiveChallenge = async () => {
      try {
        const response = await Api.get("api/challenge/participated");
        setActiveChallenges(response.data.length);
      } catch (error) {
        console.log(error);
      }
    };
    fetchActiveChallenge();
  }, []);

  // 로그아웃 모달 열기
  function isChooseModaOpenlHandler() {
    setIsChooseModal(true);
  }

  // 로그아웃 모달 닫기
  function isChooseModalCloseHandler() {
    setIsChooseModal(false);
  }

  // 로그아웃 후 홈 이동
  function closeIsShowModalHandler() {
    setIsShowModal(false);
    nav("/home");
  }

  // 로그아웃 핸들러
  async function logoutHandler() {
    try {
      const response = await Api.post("api/auth/logout");
      if (response.data.status === "success") {
        dispatch(resetUser());
        setIsShowModal(true);
      }
    } catch (error) {
      console.log(error);
      window.alert("에러: 잠시 후 시도해주세요");
    }
  }

  return (
    <div className="flex flex-col justify-center gap-5 min-w-[345px] mt-[50px] mb-[30px]">
      <CustomHeader title="마이 페이지" />

      {/* 유저 정보 */}
      <div className="w-full flex items-center p-4 bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px]">
        <div className="mr-4">
          <img src={profileImg} alt="유저 이미지" className="w-[80px] h-[80px] rounded-full" />
        </div>
        <div className="flex flex-col">
          <span className="font-semibold">{nickName}</span>
          <span className="text-sm">{email}</span>
        </div>
      </div>

      {/* 예치금 */}
      <div className="relative">
        <p className="text-left font-semibold">예치금</p>
        {/* 예치금 박스 */}
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex items-center p-4 mt-2 relative" onClick={modalHandler}>
          <div className="flex items-center justify-center mx-auto">
            <p className="text-xl font-semibold">총 금액 :</p>
            <p className="text-xl font-semibold ml-2">{parseInt(deposit, 10).toLocaleString()}원</p>
          </div>
          <div className="absolute right-4">
            <MoreIcon />
          </div>
        </div>
        <CustomModal title="예치금" isOpen={isModalOpen} onClose={onCloseModal}>
          {/* 탭 버튼 영역 */}
          <div className="w-full flex mb-4">
            <button onClick={() => setMode("charge")}
              className={mode === "charge" ? "w-full smallButton" : "w-full smallWhiteButton"}>
              충전
            </button>
            <button onClick={() => setMode("refund")}
              className={mode === "refund" ? "w-full smallButton" : "w-full smallWhiteButton"}>
              환불
            </button>
            <button onClick={() => setMode("history")}
              className={mode === "history" ? "w-full smallButton" : "w-full smallWhiteButton"}>
              내역
            </button>
          </div>
          {/* 탭 내용 영역 - modal 내부 높이 고정 (예: h-[350px]) */}
          <div className="w-full h-[350px] overflow-y-auto">
            {mode === "charge" && (
              <div className="flex flex-col gap-4 items-center">
                <div className="w-full text-center">
                  <p className="text-sm text-gray-500">예치금 충전은 대표계좌에서 자동으로 충전됩니다</p>
                </div>
                <div className="flex justify-center items-center gap-2">
                  <p className="font-semibold text-xl">예치금 잔액 :</p>
                  <p className="font-semibold text-xl">{parseInt(deposit, 10).toLocaleString()}</p>
                </div>
                <div className="w-full flex flex-col gap-3">
                  <label className="font-semibold text-lg">충전할 금액 :</label>
                  <input
                    type="text"
                    placeholder="충전할 금액을 입력해주세요"
                    className="w-full p-2 bg-gray-200 rounded-[6px] shadow-[1px_1px_5px_rgba(0,0,0,0.05)]"
                    value={inputChange}
                    onChange={inputHandler}
                  />
                </div>
                <button className="customButton" onClick={chargeHandler}>
                  충전하기
                </button>
              </div>
            )}
            {mode === "refund" && (
              <div className="flex flex-col gap-4 items-center">
                <div className="w-full text-center">
                  <p className="text-sm text-gray-500">예치금 환불은 대표계좌에 자동으로 환불됩니다</p>
                </div>
                <div className="flex justify-center items-center gap-2">
                  <p className="font-semibold text-xl">예치금 잔액 :</p>
                  <p className="font-semibold text-xl">{parseInt(deposit, 10).toLocaleString()}</p>
                </div>
                <div className="w-full flex flex-col gap-3">
                  <label className="font-semibold text-lg">환불할 금액 :</label>
                  <input
                    type="text"
                    placeholder="환불할 금액을 입력해주세요"
                    className="w-full p-2 bg-gray-200 rounded-[6px] shadow-[1px_1px_5px_rgba(0,0,0,0.05)]"
                    value={inputChange}
                    onChange={inputHandler}
                  />
                </div>
                <button className="customButton" onClick={refundHandler}>
                  환불하기
                </button>
              </div>
            )}
            {mode === "history" && (
              <div className="flex flex-col gap-4">
                {Object.keys(groupedTransactions).length === 0 ? (
                  <p className="text-center text-gray-500">거래 내역이 없습니다.</p>
                ) : (
                  Object.keys(groupedTransactions)
                    .sort((a, b) => new Date(b) - new Date(a))
                    .map(date => (
                      <div key={date} className="mb-4">
                        <p className="font-bold text-base text-gray-700 mb-2">{date}</p>
                        {groupedTransactions[date].map((item, index) => (
                          <div key={index} className="bg-white rounded-[6px] shadow p-3 mb-2">
                            <div className="flex justify-between items-center mb-1">
                              <span className="text-sm font-semibold text-blue-500">
                                {categoryMapping[item.category] || item.category}
                              </span>
                              <span className="text-xs text-gray-400">
                                {new Date(item.transactionDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                              </span>
                            </div>
                            <p className="text-sm text-gray-600">{item.description}</p>
                          </div>
                        ))}
                      </div>
                    ))
                )}
              </div>
            )}
          </div>
        </CustomModal>
      </div>

      {/* 챌린지 현황 */}
      <div className="relative">
        <p className="text-left font-semibold">첼린지 현황</p>
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[25px] flex justify-between items-center p-4 mt-2">
          <div className="flex flex-col items-center w-1/3">
            <p className="text-xl font-semibold">{pendingChallenges}</p>
            <p className="text-sm text-gray-500">시작 전</p>
          </div>
          <div className="flex flex-col items-center w-1/3">
            <p className="text-xl font-semibold text-red-500">{activeChallenges}</p>
            <p className="text-sm text-red-500">진행 중</p>
          </div>
          <div className="flex flex-col items-center w-1/3">
            <p className="text-xl font-semibold">{completedChallenges}</p>
            <p className="text-sm text-gray-500">완료</p>
          </div>
        </div>
        <div className="absolute -top-4 right-4">
          <YellowCat />
        </div>
      </div>

      {/* 마이 메뉴 */}
      <div className="flex flex-col gap-4">
        <p className="text-left font-semibold">마이 메뉴</p>
        <Link to="correction">
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
            <div className="mr-4">
              <CorrectionIcon />
            </div>
            <p className="font-regular">회원정보 수정</p>
          </div>
        </Link>
        <Link to="account">
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
            <div className="mr-4">
              <AccountIcon />
            </div>
            <p className="font-regular">대표 계좌 설정</p>
          </div>
        </Link>
        <Link to="card">
          <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4">
            <div className="mr-4">
              <CardIcon />
            </div>
            <p className="font-regular">등록 카드 설정</p>
          </div>
        </Link>
        <div className="w-full bg-white shadow-[1px_1px_5px_rgba(0,0,0,0.05)] rounded-[6px] flex gap-5 items-center p-4" onClick={isChooseModaOpenlHandler}>
          <div className="mr-4">
            <LogoutIcon />
          </div>
          <p className="font-regular">로그아웃</p>
        </div>
        <ChooseAlertModal title="로그아웃" height={200} isClose={isChooseModalCloseHandler} isOpen={isChooseModal} isFunctionHandler={logoutHandler}>
          <p>로그아웃 하시겠습니까?</p>
        </ChooseAlertModal>
        <AlertModal title="로그아웃" height={170} isClose={closeIsShowModalHandler} isOpen={isShowModal}>
          <p className="text-xl">로그아웃 되었습니다</p>
        </AlertModal>
      </div>
    </div>
  );
}
