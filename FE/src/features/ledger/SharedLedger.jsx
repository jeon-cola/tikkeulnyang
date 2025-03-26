import { useState } from "react";
import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import LedgerHeader from "./components/LedgerHeader";
import AddUser from "./assets/AddUser.png";
import BlackCat from "./assets/LedgerCat.png";
import Modal from "@/components/Modal";

export default function SharedLedger() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  // false : 모달이 닫힌 상태
  // isModalOpen :  모달 열린 여부 체크(닫:false, 여:ture)
  // setIsModalOpen: 모달 열기/닫기

  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />
        <div className="relative">
          {/* flex : 왼쪽부터 차례대로 배치 */}
          <div className="flex">
            <img
              className="w-[20%] cursor-pointer"
              src={AddUser}
              alt="사용자 추가"
              onClick={() => setIsModalOpen(true)}
            />
            <img
              className="absolute -top-1 right-3 z-10 w-[20%] h-auto "
              src={BlackCat}
              alt="캣 이미지"
            />
          </div>
          <CustomCalendar className="z-0" />
        </div>

        {isModalOpen && (
          <Modal
            title="친구초대 링크생성"
            description="sflaskl;dskfl;dskfl;dkfl;afklaskf;laskf;lasdkf;lasdkflsdk"
            onClose={() => setIsModalOpen(false)}
          />
        )}
      </Container>
    </div>
  );
}
