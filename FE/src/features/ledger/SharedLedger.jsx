import { useState } from "react";
import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import LedgerHeader from "./components/LedgerHeader";
import AddUser from "./assets/add_user.png";
import BlackCat from "./assets/ledger_cat.png";
import Modal from "@/components/Modal";

export default function SharedLedger() {
  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />
        <div className="relative">
          {/* 사용자 추가 버튼 */}
          <div className="flex">
            <img
              className="w-[20%] cursor-pointer"
              src={AddUser}
              alt="사용자 추가"
              onClick={() => setIsModalOpen(true)} // 모달 열기
            />
            <img
              className="absolute -top-1 right-3 z-10 w-[20%] h-auto"
              src={BlackCat}
              alt="캣 이미지"
            />
          </div>

          <CustomCalendar className="z-0" />

          {/* 모달 컴포넌트 렌더링 */}
          {isModalOpen && (
            <Modal
              title="사용자 추가"
              description="함께 가계부를 작성할 사용자를 추가하세요."
              onClose={() => setIsModalOpen(false)} // 모달 닫기
            />
          )}
        </div>
      </Container>
    </div>
  );
}
