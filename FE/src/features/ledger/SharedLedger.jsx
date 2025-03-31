import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import LedgerHeader from "./components/LedgerHeader";
import AddUser from "./assets/add_user.png";
import BlackCat from "./assets/ledger_cat.png";

export default function SharedLedger() {
  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />
        <div className="relative">
          {/* flex : 왼쪽부터 차례대로 배치치 */}
          <div className="flex">
            <img className="w-[20%]" src={AddUser} alt="사용자 추가" />
            <img
              className="absolute -top-1 right-3 z-10 w-[20%] h-auto "
              src={BlackCat}
              alt="캣 이미지"
            />
          </div>
          <CustomCalendar className="z-0" />
        </div>
      </Container>
    </div>
  );
}
