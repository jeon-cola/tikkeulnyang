import Container from "@/components/Container";
import CustomCalendar from "@/components/CustomCalendar";
import LedgerHeader from "./components/LedgerHeader";

export default function PersonalLedger() {
  return (
    <div className="w-full">
      <Container>
        {/* 헤더를 생성해서 달력 위 컴포넌트 관리 */}
        <LedgerHeader />
        <CustomCalendar />
      </Container>
    </div>
  );
}
