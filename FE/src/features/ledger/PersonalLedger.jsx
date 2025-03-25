import Container from "@/components/Container";
import PersonalLedgerCalendar from "./components/PersonalLedgerCalendar";
import LedgerHeader from "./components/LedgerHeader";

export default function PersonalLedger() {
  return (
    <div className="w-full">
      <Container>
        <LedgerHeader />
        <PersonalLedgerCalendar />
      </Container>
    </div>
  );
}
