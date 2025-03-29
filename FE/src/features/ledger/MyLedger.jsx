import Container from "@/components/Container";
import LedgerCalendar from "./components/LedgerCalendar";
import LedgerHeader from "./components/LedgerHeader";
import Cat from "./assets/black_cat.png";
export default function PersonalLedger() {
  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />
        <div className="relative">
          <img
            className="top-2 right-10 w-[50px] absolute z-10"
            src={Cat}
            alt="고양이 이미지"
          />
          <div className="z-0">
            <LedgerCalendar />
          </div>
        </div>
      </Container>
    </div>
  );
}
